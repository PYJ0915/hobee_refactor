// ============================================================
//  memberProfile.js  —  팔로우 + 1:1 채팅 (WebSocket/STOMP)
// ============================================================

document.addEventListener("DOMContentLoaded", () => {

	// -------------------------------------------------------
	//  공통 변수 (Thymeleaf에서 memberProfile.html로 주입)
	//  const targetMemberNo = /*[[${targetMember.memberNo}]]*/ null;
	//  const loginMemberNo  = /*[[${session.loginMember?.memberNo}]]*/ null;
	//  const loginNickname  = /*[[${session.loginMember?.memberNickname}]]*/ null;
	//  const targetNickname = /*[[${targetMember.memberNickname}]]*/ null;
	// -------------------------------------------------------

	// 팔로우 모달에서 쓸 memberNo
	// 내 프로필이면 loginMemberNo, 남의 프로필이면 targetMemberNo
	const profileMemberNo = isMyProfile ? loginMemberNo : targetMemberNo;

	// ========================
	//  팔로우 버튼
	// ========================

	const followBtn = document.querySelector("#followBtn");

	if (followBtn != null) {
		followBtn.addEventListener("click", async () => {

			const targetNo = followBtn.dataset.followingNo;
			const isFollowing = followBtn.dataset.isFollowing === "true";

			try {
				// 컨트롤러: POST /follow/toggle + @RequestBody { followingNo }
				const result = await fetch("/follow/toggle", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ followingNo: parseInt(targetNo) })
				}).then(resp => resp.json());

				const nowFollowing = result.isFollowing;
				followBtn.dataset.isFollowing = nowFollowing;
				followBtn.classList.toggle("following", nowFollowing);
				followBtn.querySelector("span").textContent =
					nowFollowing ? "팔로잉 ✓" : "팔로우";

				// 팔로우 → 채팅 버튼 노출 / 언팔로우 → 채팅 버튼 숨김
				const chatBtn = document.querySelector("#chatBtn");
				if (chatBtn) {
					chatBtn.style.display = nowFollowing ? "flex" : "none";
				}

				// 팔로워 수 갱신 (서버에서 최신 수 반환)
				const followerCount = document.querySelector("#followerCount");
				if (followerCount && result.followerCount !== undefined) {
					followerCount.textContent = result.followerCount;
				}

			} catch (err) {
				console.error("팔로우 요청 오류:", err);
			}
		});
	}

	// ========================
	//  팔로워/팔로잉 모달
	// ========================

	const followerStat = document.querySelector("#followerStat");
	const followingStat = document.querySelector("#followingStat");
	const followModal = document.querySelector("#followModal");
	const followModalTitle = document.querySelector("#followModalTitle");
	const followModalList = document.querySelector("#followModalList");
	const followModalClose = document.querySelector("#followModalClose");
	const followModalBackdrop = document.querySelector("#followModalBackdrop");

	async function openFollowModal(type) {
		if (!followModal) return;

		followModalTitle.textContent = type === "followers" ? "팔로워" : "팔로잉";
		followModalList.innerHTML = '<li class="follow-modal-empty">불러오는 중...</li>';
		followModal.classList.remove("popup-hidden");

		try {
			// 컨트롤러: GET /follow/followers/{memberNo} or /follow/followings/{memberNo}
			const data = await fetch(`/follow/${type}/${profileMemberNo}`)
				.then(resp => resp.json());

			followModalList.innerHTML = "";

			if (!data || data.length === 0) {
				followModalList.innerHTML = '<li class="follow-modal-empty">목록이 없습니다.</li>';
				return;
			}

			data.forEach(m => {
				// Follow DTO: followerNo, followingNo, memberNickname, profileImg
				// 팔로워 목록: 나를 팔로우하는 사람 → followerNo가 상대 memberNo
				// 팔로잉 목록: 내가 팔로우하는 사람 → followingNo가 상대 memberNo
				const memberNo = type === "followers" ? m.followerNo : m.followingNo;

				const li = document.createElement("li");
				li.className = "follow-modal-item";
				li.innerHTML = `
                    <a href="/member/profile/${memberNo}" class="follow-modal-link">
                        <img src="${m.profileImg || '/images/user.png'}"
                             class="follow-modal-img" alt="프로필">
                        <span class="follow-modal-name">${m.memberNickname}</span>
                    </a>
                `;
				followModalList.appendChild(li);
			});

		} catch (err) {
			followModalList.innerHTML = '<li class="follow-modal-empty">불러오기 실패</li>';
			console.error("팔로우 목록 오류:", err);
		}
	}

	function closeFollowModal() {
		if (followModal) followModal.classList.add("popup-hidden");
	}

	if (followerStat) followerStat.addEventListener("click", () => openFollowModal("followers"));
	if (followingStat) followingStat.addEventListener("click", () => openFollowModal("followings"));
	if (followModalClose) followModalClose.addEventListener("click", closeFollowModal);
	if (followModalBackdrop) followModalBackdrop.addEventListener("click", closeFollowModal);


	// ============================================================
	//  1:1 채팅 (WebSocket + STOMP)
	// ============================================================

	const chatBtn = document.querySelector("#chatBtn");
	const chatModal = document.querySelector("#chatModal");
	const chatModalClose = document.querySelector("#chatModalClose");
	const chatModalBackdrop = document.querySelector("#chatModalBackdrop");
	const chatMessageList = document.querySelector("#chatMessageList");
	const chatInput = document.querySelector("#chatInput");
	const chatSendBtn = document.querySelector("#chatSendBtn");
	const chatStatusDot = document.querySelector("#chatStatusDot");
	let isConnecting  = false;

	let stompClient = null;
	let currentRoomId = null;

	// ---------------------------
	//  채팅 모달 열기
	// ---------------------------
	if (chatBtn != null) {
		
		chatBtn.addEventListener("click", async () => {
			chatBtn.disabled = true;
			chatModal.classList.remove("popup-hidden");

			try {
				// 채팅방 조회 or 생성
				const result = await fetch(`/chat/direct/${targetMemberNo}`, {
					method: "POST"
				}).then(resp => resp.json());

				currentRoomId = result.roomId;

				await loadPreviousMessages(currentRoomId);
				connectWebSocket();

			} catch (err) {
				console.error("채팅방 개설 오류:", err);
				appendSystemMessage("채팅방 연결에 실패했습니다.");
			} finally {
				chatBtn.disabled = false;
			}
		});
	}

	// ---------------------------
	//  채팅 모달 닫기
	// ---------------------------
	function closeChatModal() {
		chatModal.classList.add("popup-hidden");
		disconnectWebSocket();
		currentRoomId = null;
	}

	if (chatModalClose) chatModalClose.addEventListener("click", closeChatModal);
	if (chatModalBackdrop) chatModalBackdrop.addEventListener("click", closeChatModal);

	// ---------------------------
	//  이전 메시지 불러오기
	// ---------------------------
	async function loadPreviousMessages(roomId) {

		chatMessageList.innerHTML = '<li class="chat-loading"><span>대화 내용을 불러오는 중...</span></li>';

		try {
			const messages = await fetch(`/chat/room/${roomId}/messages`)
				.then(resp => resp.json());

			chatMessageList.innerHTML = "";

			if (!messages || messages.length === 0) {
				chatMessageList.innerHTML =
					'<li class="chat-empty">아직 대화 내용이 없습니다.<br>첫 메시지를 보내보세요!</li>';
				return;
			}

			let lastDate = null;

			messages.forEach(msg => {
				const msgDate = msg.sendTime ? msg.sendTime.substring(0, 10) : null;
				if (msgDate && msgDate !== lastDate) {
					appendDateDivider(msgDate);
					lastDate = msgDate;
				}
				appendMessage(msg);
			});

			scrollToBottom();

		} catch (err) {
			chatMessageList.innerHTML =
				'<li class="chat-empty">메시지를 불러오지 못했습니다.</li>';
			console.error(err);
		}
	}

	// ---------------------------
	//  WebSocket 연결
	// ---------------------------
	function connectWebSocket() {
		
		if (isConnecting) return;

		// 기존 연결이 살아있으면 먼저 끊기
		if (stompClient !== null && stompClient.connected) {
			stompClient.disconnect();
			stompClient = null;
		}
		
		isConnecting = true;

		const socket = new SockJS('/ws');
		stompClient = Stomp.over(socket);
		stompClient.debug = null;

		const headers = { 'nickname': loginNickname };

		stompClient.connect(headers, (frame) => {

			isConnecting = false;
			console.log("WebSocket 연결됨:", frame);
			chatStatusDot.classList.add("connected");

			stompClient.subscribe("/topic/chat/room/" + currentRoomId, (message) => {
				const chatMessage = JSON.parse(message.body);

				// 내가 보낸 메시지는 sendMessage()에서 이미 추가했으므로 무시
				if (String(chatMessage.senderNo) === String(loginMemberNo)) return;

				appendMessage(chatMessage);
				scrollToBottom();
			});

		}, (error) => {
			isConnecting = false;
			console.error("WebSocket 연결 오류:", error);
			chatStatusDot.classList.remove("connected");
			appendSystemMessage("연결이 끊겼습니다. 페이지를 새로고침 해주세요.");
		});
	}

	// ---------------------------
	//  WebSocket 연결 해제
	// ---------------------------
	function disconnectWebSocket() {
		isConnecting = false;
	    if (stompClient !== null) {
	        if (stompClient.connected) {
	            stompClient.disconnect();
	        }
	        stompClient = null; // connected 여부와 관계없이 즉시 null 처리
	    }
	    chatStatusDot.classList.remove("connected");
		console.log("WebSocket 연결 해제");
	}

	// ---------------------------
	//  메시지 전송
	// ---------------------------
	function sendMessage() {

		const content = chatInput.value.trim();
		if (!content) return;
		if (!stompClient || !stompClient.connected) {
			appendSystemMessage("연결이 끊겼습니다. 잠시 후 다시 시도해주세요.");
			return;
		}

		const now = new Date();
		const sendTime = now.getFullYear() + "-"
			+ String(now.getMonth() + 1).padStart(2, "0") + "-"
			+ String(now.getDate()).padStart(2, "0") + " "
			+ String(now.getHours()).padStart(2, "0") + ":"
			+ String(now.getMinutes()).padStart(2, "0") + ":"
			+ String(now.getSeconds()).padStart(2, "0");

		const message = {
			type: "TALK",
			roomId: currentRoomId,
			senderNo: loginMemberNo,
			sender: loginNickname,
			receiver: targetNickname,  // Principal 식별자 (닉네임)
			content: content
		};

		stompClient.send("/app/chat/direct", {}, JSON.stringify(message));

		// 내가 보낸 메시지를 직접 화면에 추가
		appendMessage(message);
		scrollToBottom();

		chatInput.value = "";
		chatInput.focus();
	}

	if (chatSendBtn) {
		chatSendBtn.addEventListener("click", sendMessage);
	}

	if (chatInput) {
		chatInput.addEventListener("keydown", (e) => {
			if (e.key === "Enter" && !e.shiftKey) {
				e.preventDefault();
				sendMessage();
			}
		});
	}

	// ---------------------------
	//  메시지 화면에 추가
	// ---------------------------
	function appendMessage(msg) {

		const loading = chatMessageList.querySelector(".chat-loading, .chat-empty");
		if (loading) loading.remove();

		const isMine = String(msg.senderNo) === String(loginMemberNo);

		const li = document.createElement("li");
		li.className = `chat-msg-item ${isMine ? "mine" : "other"}`;

		li.innerHTML = `
            <div class="chat-bubble">${escapeHtml(msg.content)}</div>
            <span class="chat-time">${formatTime(msg.sendTime)}</span>
        `;

		chatMessageList.appendChild(li);
	}

	function appendSystemMessage(text) {
		const li = document.createElement("li");
		li.className = "chat-system-msg";
		li.textContent = text;
		chatMessageList.appendChild(li);
		scrollToBottom();
	}

	function appendDateDivider(dateStr) {
		const li = document.createElement("li");
		li.className = "chat-date-divider";
		li.textContent = dateStr;
		chatMessageList.appendChild(li);
	}

	// ---------------------------
	//  유틸
	// ---------------------------
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
			const hour = h % 12 || 12;
			return `${ampm} ${hour}:${m}`;
		} catch {
			return sendTime;
		}
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
