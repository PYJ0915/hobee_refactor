document.addEventListener("DOMContentLoaded", async () => {

    // ========================
    //  변수
    // ========================
    let stompClient = null;
    let currentRoomId = null;
    let isConnecting = false;
    let searchTimer = null;

    const chatRoomList = document.querySelector("#chatRoomList");
    const chatWindow = document.querySelector("#chatWindow");
    const chatPlaceholder = document.querySelector("#chatPlaceholder");
    const chatMessageList = document.querySelector("#chatMessageList");
    const chatInput = document.querySelector("#chatInput");
    const chatSendBtn = document.querySelector("#chatSendBtn");
    const chatStatusDot = document.querySelector("#chatStatusDot");
    const chatWindowName = document.querySelector("#chatWindowName");
    const chatWindowAvatar = document.querySelector("#chatWindowAvatar");
    const chatWindowCount = document.querySelector("#chatWindowCount");
    const chatLeaveBtn = document.querySelector("#chatLeaveBtn");

    // ========================
    //  채팅방 목록 로드
    // ========================
    await loadRoomList();

    async function loadRoomList() {
        try {
            const rooms = await fetch("/chat/rooms").then(r => r.json());
            chatRoomList.innerHTML = "";

            if (!rooms || rooms.length === 0) {
                chatRoomList.innerHTML =
                    '<li class="chat-room-empty">채팅방이 없습니다.</li>';
                return;
            }

            rooms.forEach(room => {
                const li = document.createElement("li");
                li.className = "chat-room-item";
                li.dataset.roomId = room.roomNo;
                li.dataset.roomType = room.roomType;
                li.dataset.roomName = room.roomName;
                li.dataset.roomImg = room.roomProfileImg || defaultImg;

                const unread = room.unreadCount > 0
                    ? `<span class="room-unread">${room.unreadCount}</span>`
                    : "";

                const memberCount = room.roomType === "GROUP"
                    ? `<span class="room-member-count">${room.memberCount}명</span>`
                    : "";

                li.innerHTML = `
                    <img src="${room.roomProfileImg || defaultImg}"
                         class="room-avatar" alt="프로필">
                    <div class="room-info">
                        <div class="room-info-top">
                            <span class="room-name">${room.roomName}</span>
                            ${memberCount}
                            ${unread}
                        </div>
                        <span class="room-last-msg">
                            ${room.lastMessage || "대화가 없습니다."}
                        </span>
                    </div>
                    <span class="room-time">
                        ${room.lastMessageTime || ""}
                    </span>
                `;

                li.addEventListener("click", () => openRoom(room));
                chatRoomList.appendChild(li);
            });

        } catch (err) {
            console.error("채팅방 목록 오류:", err);
        }
    }

    // ========================
    //  채팅방 열기
    // ========================
    async function openRoom(room) {

        // 이전 방 연결 해제
        if (currentRoomId && currentRoomId !== room.roomNo) {
            disconnectWebSocket();
        }

        currentRoomId = room.roomNo;

        // 헤더 업데이트
        chatWindowName.textContent = room.roomName;
        chatWindowAvatar.src = room.roomProfileImg || defaultImg;
        chatWindowCount.textContent = room.roomType === "GROUP"
            ? `${room.memberCount}명` : "";

        // 화면 전환
        chatPlaceholder.classList.add("popup-hidden");
        chatWindow.classList.remove("popup-hidden");

        // 활성화 표시
        document.querySelectorAll(".chat-room-item")
            .forEach(el => el.classList.remove("active"));
        document.querySelector(`[data-room-id="${room.roomNo}"]`)
            ?.classList.add("active");

        // 이전 메시지 로드
        await loadMessages(room.roomNo);

        // 읽음 처리
        await fetch(`/chat/room/${room.roomNo}/read`, { method: "POST" });

        // 뱃지 제거
        const roomEl = document.querySelector(`[data-room-id="${room.roomNo}"]`);
        if (roomEl) roomEl.querySelector(".room-unread")?.remove();

        // WebSocket 연결
        connectWebSocket(room.roomNo);
    }

    // ========================
    //  메시지 로드
    // ========================
    async function loadMessages(roomId) {
        chatMessageList.innerHTML =
            '<li class="chat-empty">불러오는 중...</li>';

        try {
            const messages = await fetch(`/chat/room/${roomId}/messages`)
                .then(r => r.json());

            chatMessageList.innerHTML = "";

            if (!messages || messages.length === 0) {
                chatMessageList.innerHTML =
                    '<li class="chat-empty">첫 메시지를 보내보세요!</li>';
                return;
            }

            let lastDate = null;
            messages.forEach(msg => {
                const date = msg.sendTime?.substring(0, 10);
                if (date && date !== lastDate) {
                    appendDateDivider(date);
                    lastDate = date;
                }
                if (msg.senderNo === 0) {
                    appendSystemMessage(msg.content);
                } else {
                    appendMessage(msg);
                }
            });

            scrollToBottom();

        } catch (err) {
            console.error("메시지 로드 오류:", err);
        }
    }

    // ========================
    //  WebSocket 연결
    // ========================
    function connectWebSocket(roomId) {
        if (isConnecting) return;
        if (stompClient?.connected) return;

        isConnecting = true;

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({ 'nickname': loginNickname }, (frame) => {

            isConnecting = false;
            chatStatusDot.classList.add("connected");

            stompClient.subscribe(`/topic/chat/room/${roomId}`, (msg) => {
                const chatMsg = JSON.parse(msg.body);
                console.log("수신 메시지:", chatMsg);
                if (String(chatMsg.senderNo) === String(loginMemberNo)) return;

                // LEAVE 타입이면 시스템 메시지로 표시
                if (chatMsg.type === "LEAVE") {
                    appendSystemMessage(chatMsg.content);
                } else {
                    appendMessage(chatMsg);
                }

                scrollToBottom();
                fetch(`/chat/room/${roomId}/read`, { method: "POST" });
            });

        }, (err) => {
            isConnecting = false;
            chatStatusDot.classList.remove("connected");
            console.error("WebSocket 오류:", err);
        });
    }

    function disconnectWebSocket() {
        isConnecting = false;
        if (stompClient?.connected) stompClient.disconnect();
        stompClient = null;
        chatStatusDot.classList.remove("connected");
    }

    // ========================
    //  메시지 전송
    // ========================
    function sendMessage() {
        const content = chatInput.value.trim();
        if (!content) return;
        if (!stompClient?.connected) return;

        const now = new Date();
        const sendTime = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;

        const message = {
            type: "TALK",
            roomId: currentRoomId,
            senderNo: loginMemberNo,
            sender: loginNickname,
            senderProfileImg: loginProfileImg || defaultImg,
            content: content,
            sendTime: sendTime
        };

        stompClient.send(`/app/chat/room/${currentRoomId}`, {}, JSON.stringify(message));

        appendMessage(message);
        scrollToBottom();

        chatInput.value = "";
        chatInput.focus();

        // 채팅방 목록 마지막 메시지 업데이트
        const roomEl = document.querySelector(`[data-room-id="${currentRoomId}"]`);
        if (roomEl) {
            const lastMsgEl = roomEl.querySelector(".room-last-msg");
            const timeEl = roomEl.querySelector(".room-time");
            if (lastMsgEl) lastMsgEl.textContent = content;
            if (timeEl) timeEl.textContent = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
        }
    }

    chatSendBtn.addEventListener("click", sendMessage);
    chatInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    // ========================
    //  단체 채팅방 생성 모달
    // ========================
    const createGroupBtn = document.querySelector("#createGroupBtn");
    const groupModal = document.querySelector("#groupModal");
    const groupModalClose = document.querySelector("#groupModalClose");
    const groupModalBackdrop = document.querySelector("#groupModalBackdrop");
    const groupSearchBtn = document.querySelector("#groupSearchBtn");
    const groupSearchResult = document.querySelector("#groupSearchResult");
    const groupSelectedList = document.querySelector("#groupSelectedList");
    const groupCreateConfirm = document.querySelector("#groupCreateConfirm");
    const selectedCount = document.querySelector("#selectedCount");

    let selectedMembers = []; // { memberNo, memberNickname, profileImg }

    createGroupBtn.addEventListener("click", () => {
        groupModal.classList.remove("popup-hidden");
    });

    [groupModalClose, groupModalBackdrop].forEach(el => {
        el?.addEventListener("click", () => {
            groupModal.classList.add("popup-hidden");
            selectedMembers = [];
            renderSelectedMembers();
            groupSearchResult.innerHTML = "";
            document.querySelector("#groupRoomName").value = "";
            document.querySelector("#groupMemberSearch").value = "";
        });
    });

    // 참여자 검색
    async function searchMembers() {
        const keyword = document.querySelector("#groupMemberSearch").value.trim();
        if (!keyword) {
            groupSearchResult.innerHTML = "";
            return;
        }

        try {
            const result = await fetch(`/member/search?keyword=${encodeURIComponent(keyword)}`)
                .then(r => r.json());

            groupSearchResult.innerHTML = "";

            if (!result || result.length === 0) {
                groupSearchResult.innerHTML =
                    '<li class="group-search-empty">검색 결과가 없습니다.</li>';
                return;
            }

            result.forEach(member => {
                if (member.memberNo === loginMemberNo) return;

                const li = document.createElement("li");
                li.className = "group-search-item";
                li.innerHTML = `
                <img src="${member.profileImg || defaultImg}"
                     class="group-search-img" alt="프로필">
                <span class="group-search-name">${member.memberNickname}</span>
                <button class="group-add-btn"
                        data-member-no="${member.memberNo}"
                        data-nickname="${member.memberNickname}"
                        data-img="${member.profileImg || defaultImg}">
                    추가
                </button>
            `;

                li.querySelector(".group-add-btn").addEventListener("click", (e) => {
                    const btn = e.currentTarget;
                    const no = parseInt(btn.dataset.memberNo);

                    if (selectedMembers.some(m => m.memberNo === no)) {
                        alert("이미 추가된 참여자입니다.");
                        return;
                    }

                    selectedMembers.push({
                        memberNo: no,
                        memberNickname: btn.dataset.nickname,
                        profileImg: btn.dataset.img
                    });

                    renderSelectedMembers();
                });

                groupSearchResult.appendChild(li);
            });

        } catch (err) {
            console.error("멤버 검색 오류:", err);
        }
    }

    // 버튼 클릭 검색
    groupSearchBtn.addEventListener("click", searchMembers);

    // 입력 시 자동 검색 (300ms debounce)
    document.querySelector("#groupMemberSearch").addEventListener("input", () => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(searchMembers, 300);
    });

    function renderSelectedMembers() {
        groupSelectedList.innerHTML = "";
        selectedCount.textContent = selectedMembers.length;

        selectedMembers.forEach((m, idx) => {
            const li = document.createElement("li");
            li.className = "group-selected-item";
            li.innerHTML = `
                <img src="${m.profileImg}" class="group-selected-img" alt="프로필">
                <span>${m.memberNickname}</span>
                <button class="group-remove-btn" data-idx="${idx}">✕</button>
            `;
            li.querySelector(".group-remove-btn").addEventListener("click", (e) => {
                selectedMembers.splice(parseInt(e.currentTarget.dataset.idx), 1);
                renderSelectedMembers();
            });
            groupSelectedList.appendChild(li);
        });
    }

    // 채팅방 생성 확인
    groupCreateConfirm.addEventListener("click", async () => {
        const roomName = document.querySelector("#groupRoomName").value.trim();

        if (!roomName) {
            alert("채팅방 이름을 입력해주세요.");
            return;
        }
        if (selectedMembers.length === 0) {
            alert("참여자를 1명 이상 추가해주세요.");
            return;
        }

        try {
            const result = await fetch("/chat/group", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    roomName: roomName,
                    memberNos: selectedMembers.map(m => m.memberNo)
                })
            }).then(r => r.json());

            const totalCount = selectedMembers.length + 1;

            groupModal.classList.add("popup-hidden");
            selectedMembers = [];

            // 목록 새로고침 후 새 방 열기
            await loadRoomList();
            const newRoom = {
                roomNo: result.roomId,
                roomType: "GROUP",
                roomName: roomName,
                roomProfileImg: null,
                memberCount: totalCount
            };
            openRoom(newRoom);

        } catch (err) {
            console.error("채팅방 생성 오류:", err);
        }
    });

    // 채팅방 나가기
    chatLeaveBtn.addEventListener("click", async () => {
        if (!confirm("채팅방에서 나가시겠습니까?")) return;
        await fetch(`/chat/room/${currentRoomId}/leave`, { method: "POST" });
        disconnectWebSocket();
        chatWindow.classList.add("popup-hidden");
        chatPlaceholder.classList.remove("popup-hidden");
        currentRoomId = null;
        await loadRoomList();
    });

    // ========================
    //  유틸 함수
    // ========================
    function appendMessage(msg) {
        const loading = chatMessageList.querySelector(".chat-empty");
        if (loading) loading.remove();

        const isMine = String(msg.senderNo) === String(loginMemberNo);
        const li = document.createElement("li");
        li.className = `chat-msg-item ${isMine ? "mine" : "other"}`;

        // 상대방 메시지일 때만 닉네임 + 아바타 표시
        const senderInfo = !isMine ? `
        <div class="chat-sender-info">
            <img src="${msg.senderProfileImg || defaultImg}" class="chat-sender-avatar" alt="프로필">
            <span class="chat-sender-name">${msg.sender || ''}</span>
        </div>` : '';

        li.innerHTML = `
        ${senderInfo}
        <div class="chat-bubble">${escapeHtml(msg.content)}</div>
        <span class="chat-time">${formatTime(msg.sendTime)}</span>
    `;
        chatMessageList.appendChild(li);
    }

    function appendDateDivider(dateStr) {
        const li = document.createElement("li");
        li.className = "chat-date-divider";
        li.textContent = dateStr;
        chatMessageList.appendChild(li);
    }

    function scrollToBottom() {
        chatMessageList.scrollTop = chatMessageList.scrollHeight;
    }

    function formatTime(sendTime) {
        if (!sendTime) return "";
        try {
            const date = new Date(sendTime);
            const h = date.getHours();
            const m = String(date.getMinutes()).padStart(2, "0");
            const ampm = h >= 12 ? "오후" : "오전";
            return `${ampm} ${h % 12 || 12}:${m}`;
        } catch { return sendTime; }
    }

    function escapeHtml(str) {
        return str
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
    }

    window.addEventListener("beforeunload", disconnectWebSocket);
});

function appendSystemMessage(text) {
    const li = document.createElement("li");
    li.className = "chat-system-msg";
    li.textContent = text;
    chatMessageList.appendChild(li);
}