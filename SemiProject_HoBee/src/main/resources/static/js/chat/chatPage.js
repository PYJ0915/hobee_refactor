document.addEventListener("DOMContentLoaded", async () => {

	// ========================
	//  변수
	// ========================
	let stompClient = null;
	let currentRoomId = null;
	let isConnecting = false;
	let searchTimer = null;
	let typingTimer = null;      // 입력 멈춤 감지 타이머
	let isTypingSent = false;    // TYPING 중복 전송 방지 플래그
	const typingUsers = {};      // { senderNo: { nickname, timer } }
	const TYPING_TIMEOUT = 3000; // 3초 뒤 자동 제거
	let inviteSearchTimer = null;
	let pendingImgFile = null; // 업로드 대기 중인 이미지 파일
	let currentRoomType = null; // 현재 방 타입 저장

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
	const inviteModal = document.querySelector("#inviteModal");
	const inviteModalClose = document.querySelector("#inviteModalClose");
	const inviteModalBackdrop = document.querySelector("#inviteModalBackdrop");
	const inviteCurrentList = document.querySelector("#inviteCurrentList");
	const inviteSearchResult = document.querySelector("#inviteSearchResult");
	const inviteSearchInput = document.querySelector("#inviteSearchInput");
	const inviteSearchBtn = document.querySelector("#inviteSearchBtn");
	const chatInviteBtn = document.querySelector("#chatInviteBtn");
	const chatRenameBtn = document.querySelector("#chatRenameBtn");
	const roomSettingModal = document.querySelector("#roomSettingModal");
	const roomSettingBackdrop = document.querySelector("#roomSettingBackdrop");
	const roomSettingClose = document.querySelector("#roomSettingClose");
	const roomSettingCancel = document.querySelector("#roomSettingCancel");
	const roomSettingConfirm = document.querySelector("#roomSettingConfirm");
	const roomSettingNameInput = document.querySelector("#roomSettingNameInput");
	const roomSettingImgPreview = document.querySelector("#roomSettingImgPreview");
	const roomImgInput = document.querySelector("#roomImgInput");


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
		currentRoomType = room.roomType;

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

		// 단체 채팅방이면 초대 버튼 표시
		const chatInviteBtn = document.querySelector("#chatInviteBtn");
		if (chatInviteBtn) {
			if (room.roomType === "GROUP") {
				chatInviteBtn.classList.remove("popup-hidden");
			} else {
				chatInviteBtn.classList.add("popup-hidden");
			}
		}
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
				console.log(msg);
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
				if (String(chatMsg.senderNo) === String(loginMemberNo)) return;

				if (chatMsg.type === "TYPING") {
					showTypingIndicator(chatMsg.senderNo, chatMsg.sender);

				} else if (chatMsg.type === "STOP_TYPING") {
					removeTypingIndicator(chatMsg.senderNo);

				} else if (
					chatMsg.type === "LEAVE" ||
					chatMsg.type === "ENTER" ||
					chatMsg.type === "SYSTEM"
				) {
					appendSystemMessage(chatMsg.content);
					scrollToBottom();

				} else {
					// 메시지가 오면 그 사람의 타이핑 표시 즉시 제거
					removeTypingIndicator(chatMsg.senderNo);
					appendMessage(chatMsg);
					scrollToBottom();
					fetch(`/chat/room/${roomId}/read`, { method: "POST" });
				}
			});

		}, (err) => {
			isConnecting = false;
			chatStatusDot.classList.remove("connected");
			console.error("WebSocket 오류:", err);
		});
	}

	function disconnectWebSocket() {
		if (isTypingSent && stompClient?.connected && currentRoomId) sendStopTyping();
		clearAllTypingIndicators();
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

		sendStopTyping();

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

	// 타이핑 감지
	chatInput.addEventListener("input", () => {
		if (!stompClient?.connected || !currentRoomId) return;

		if (!isTypingSent) sendTyping();

		clearTimeout(typingTimer);
		typingTimer = setTimeout(() => sendStopTyping(), TYPING_TIMEOUT);
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

		if (selectedMembers.length === 0) {
			alert("참여자를 1명 이상 추가해주세요.");
			return;
		}

		try {
			const result = await fetch("/chat/group", {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({
					roomName: roomName, // 빈 문자열이면 서버에서 자동 생성
					memberNos: selectedMembers.map(m => m.memberNo)
				})
			}).then(r => r.json());

			const totalCount = selectedMembers.length + 1;
			const finalRoomName = result.roomName; // 서버에서 반환된 최종 이름

			groupModal.classList.add("popup-hidden");
			selectedMembers = [];

			await loadRoomList();
			openRoom({
				roomNo: result.roomId,
				roomType: "GROUP",
				roomName: finalRoomName,
				roomProfileImg: null,
				memberCount: totalCount
			});

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

	// 채팅방 설정 모달 열기
	if (chatRenameBtn) {
		chatRenameBtn.addEventListener("click", () => {
			// 현재 이름 세팅
			roomSettingNameInput.value = chatWindowName.textContent;

			// 현재 이미지 세팅
			const roomEl = document.querySelector(`[data-room-id="${currentRoomId}"]`);
			const currentImg = roomEl?.dataset.roomImg || defaultImg;
			roomSettingImgPreview.src = currentImg;

			// 1:1 채팅방이면 이미지 업로드 영역 숨김
			const imgArea = document.querySelector(".room-setting-img-area");
			if (imgArea) {
				imgArea.style.display = currentRoomType === "GROUP" ? "flex" : "none";
			}

			pendingImgFile = null;
			roomSettingModal.classList.remove("popup-hidden");
		});
	}

	// 모달 닫기
	[roomSettingBackdrop, roomSettingClose, roomSettingCancel].forEach(el => {
		el?.addEventListener("click", () => {
			roomSettingModal.classList.add("popup-hidden");
			pendingImgFile = null;
		});
	});

	// 이미지 미리보기
	if (roomImgInput) {
		roomImgInput.addEventListener("change", (e) => {
			const file = e.target.files[0];
			if (!file) return;
			pendingImgFile = file;
			const reader = new FileReader();
			reader.onload = (ev) => { roomSettingImgPreview.src = ev.target.result; };
			reader.readAsDataURL(file);
			roomImgInput.value = ""; // 같은 파일 재선택 가능하게
		});
	}

	// 저장
	if (roomSettingConfirm) {
		roomSettingConfirm.addEventListener("click", async () => {
			const newName = roomSettingNameInput.value.trim();
			const currentName = chatWindowName.textContent.trim();

			if (!newName) { alert("채팅방 이름을 입력해주세요."); return; }
			if (newName.length > 30) { alert("채팅방 이름은 30자 이하로 입력해주세요."); return; }

			roomSettingConfirm.disabled = true;
			roomSettingConfirm.textContent = "저장 중...";

			try {
				if (newName !== currentName) {

					const nameResult = await fetch(
						`/chat/room/${currentRoomId}/name`,
						{
							method: "POST",
							headers: {
								"Content-Type": "application/json"
							},
							body: JSON.stringify({
								roomName: newName
							})
						}
					).then(r => r.json());

					if (!nameResult.success) {
						throw new Error(
							nameResult.message || "채팅방 이름 변경 실패"
						);
					}

					// 채팅창 제목 변경
					chatWindowName.textContent = nameResult.roomName;

					// 사이드바 채팅방 이름 변경
					const roomEl = document.querySelector(`[data-room-id="${currentRoomId}"]`);

					if (roomEl) {

						const nameEl =
							roomEl.querySelector(".room-name");

						if (nameEl) {
							nameEl.textContent = nameResult.roomName;
						}

						roomEl.dataset.roomName = nameResult.roomName;
					}
				}

				// 2. 이미지 변경 (단체방 + 파일 있을 때만)
				if (currentRoomType === "GROUP" && pendingImgFile) {
					const formData = new FormData();
					formData.append("roomImg", pendingImgFile);

					const imgResult = await fetch(`/chat/room/${currentRoomId}/img`, {
						method: "POST",
						body: formData
					}).then(r => r.json());

					if (imgResult.success) {
						// 헤더 아바타 갱신
						chatWindowAvatar.src = imgResult.roomImg;

						// 사이드바 아바타 갱신
						const roomEl = document.querySelector(`[data-room-id="${currentRoomId}"]`);
						if (roomEl) {
							const avatarEl = roomEl.querySelector(".room-avatar");
							if (avatarEl) avatarEl.src = imgResult.roomImg;
							roomEl.dataset.roomImg = imgResult.roomImg;
						}
					}
				}

				roomSettingModal.classList.add("popup-hidden");
				pendingImgFile = null;

			} catch (err) {
				alert("저장에 실패했습니다.");
				console.error(err);
			} finally {
				roomSettingConfirm.disabled = false;
				roomSettingConfirm.textContent = "저장";
			}
		});
	}

	// ========================
	//  채팅방 초대 모달
	// ========================
	if (chatInviteBtn) {
		chatInviteBtn.addEventListener("click", () => openInviteModal());
	}

	[inviteModalClose, inviteModalBackdrop].forEach(el => {
		el?.addEventListener("click", closeInviteModal);
	});

	async function openInviteModal() {
		inviteModal.classList.remove("popup-hidden");
		inviteCurrentList.innerHTML = '<li class="invite-loading">불러오는 중...</li>';
		inviteSearchResult.innerHTML = "";
		inviteSearchInput.value = "";

		// 현재 참여자 목록 조회
		try {
			const members = await fetch(`/chat/room/${currentRoomId}/members`)
				.then(r => r.json());

			inviteCurrentList.innerHTML = "";

			if (!members || members.length === 0) {
				inviteCurrentList.innerHTML =
					'<li class="invite-empty">참여자가 없습니다.</li>';
				return;
			}

			members.forEach(m => {
				const li = document.createElement("li");
				li.className = "invite-member-item";
				const isMine = m.memberNo === loginMemberNo;
				li.innerHTML = `
	                <img src="${m.profileImg || defaultImg}"
	                     class="invite-member-img" alt="프로필">
	                <span class="invite-member-name">
	                    ${m.memberNickname}${isMine ? " (나)" : ""}
	                </span>
	            `;
				inviteCurrentList.appendChild(li);
			});

		} catch (err) {
			console.error("참여자 목록 오류:", err);
		}
	}

	function closeInviteModal() {
		inviteModal.classList.add("popup-hidden");
	}

	// 초대 회원 검색
	async function searchInviteMembers() {
		const keyword = inviteSearchInput.value.trim();
		if (!keyword) {
			inviteSearchResult.innerHTML = "";
			return;
		}

		try {
			const result = await fetch(`/member/search?keyword=${encodeURIComponent(keyword)}`)
				.then(r => r.json());

			// 현재 참여자는 제외
			const currentMembers = await fetch(`/chat/room/${currentRoomId}/members`)
				.then(r => r.json());
			const currentNos = currentMembers.map(m => m.memberNo);

			inviteSearchResult.innerHTML = "";

			const filtered = result.filter(m =>
				!currentNos.includes(m.memberNo) && m.memberNo !== loginMemberNo
			);

			if (filtered.length === 0) {
				inviteSearchResult.innerHTML =
					'<li class="invite-empty">초대할 수 있는 회원이 없습니다.</li>';
				return;
			}

			filtered.forEach(member => {
				const li = document.createElement("li");
				li.className = "group-search-item";
				li.innerHTML = `
	                <img src="${member.profileImg || defaultImg}"
	                     class="group-search-img" alt="프로필">
	                <span class="group-search-name">${member.memberNickname}</span>
	                <button class="invite-add-btn"
	                        data-member-no="${member.memberNo}"
	                        data-nickname="${member.memberNickname}">
	                    초대
	                </button>
	            `;

				li.querySelector(".invite-add-btn").addEventListener("click", async (e) => {
					const btn = e.currentTarget;
					btn.disabled = true;
					btn.textContent = "초대 중...";

					try {
						const res = await fetch(`/chat/room/${currentRoomId}/invite`, {
							method: "POST",
							headers: { "Content-Type": "application/json" },
							body: JSON.stringify({
								memberNo: parseInt(btn.dataset.memberNo),
								nickname: btn.dataset.nickname
							})
						}).then(r => r.json());

						if (res.success) {
							btn.textContent = "초대됨 ✓";
							btn.style.background = "#6db33f";
							btn.style.color = "white";
							btn.style.border = "none";

							// 서버에서 갱신된 방 이름 반영
							if (res.roomName) {
								chatWindowName.textContent = res.roomName;
								const roomEl = document.querySelector(`[data-room-id="${currentRoomId}"]`);
								if (roomEl) {
									const nameEl = roomEl.querySelector(".room-name");
									if (nameEl) nameEl.textContent = res.roomName;
								}
							}

							// 목록 새로고침
							await loadRoomList();
						} else {
							btn.textContent = "이미 참여 중";
							btn.disabled = true;
						}
					} catch (err) {
						btn.textContent = "오류";
						btn.disabled = false;
						console.error("초대 오류:", err);
					}
				});

				inviteSearchResult.appendChild(li);
			});

		} catch (err) {
			console.error("검색 오류:", err);
		}
	}

	inviteSearchBtn.addEventListener("click", searchInviteMembers);
	inviteSearchInput.addEventListener("input", () => {
		clearTimeout(inviteSearchTimer);
		inviteSearchTimer = setTimeout(searchInviteMembers, 300);
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


	function appendSystemMessage(text) {
		const li = document.createElement("li");
		li.className = "chat-system-msg";
		li.textContent = text;
		chatMessageList.appendChild(li);
	}


	function sendTyping() {
		if (!stompClient?.connected || !currentRoomId) return;
		isTypingSent = true;
		stompClient.send("/app/chat/typing", {}, JSON.stringify({
			type: "TYPING",
			roomId: currentRoomId,
			senderNo: loginMemberNo,
			sender: loginNickname
		}));
	}

	function sendStopTyping() {
		if (!isTypingSent) return;
		isTypingSent = false;
		clearTimeout(typingTimer);
		if (!stompClient?.connected || !currentRoomId) return;
		stompClient.send("/app/chat/typing", {}, JSON.stringify({
			type: "STOP_TYPING",
			roomId: currentRoomId,
			senderNo: loginMemberNo,
			sender: loginNickname
		}));
	}

	function showTypingIndicator(senderNo, senderName) {
		if (typingUsers[senderNo]) {
			clearTimeout(typingUsers[senderNo].timer);
		} else {
			const li = document.createElement("li");
			li.className = "chat-typing-indicator";
			li.id = `typing-${senderNo}`;
			li.innerHTML = `
	            <span class="typing-name">${escapeHtml(senderName)}</span>
	            <span class="typing-dots">
	                <span></span><span></span><span></span>
	            </span>
	        `;
			chatMessageList.appendChild(li);
			scrollToBottom();
		}

		typingUsers[senderNo] = {
			nickname: senderName,
			timer: setTimeout(() => removeTypingIndicator(senderNo), TYPING_TIMEOUT + 500)
		};
	}

	function removeTypingIndicator(senderNo) {
		if (!typingUsers[senderNo]) return;
		clearTimeout(typingUsers[senderNo].timer);
		delete typingUsers[senderNo];
		document.getElementById(`typing-${senderNo}`)?.remove();
	}

	function clearAllTypingIndicators() {
		Object.keys(typingUsers).forEach(no => {
			clearTimeout(typingUsers[no].timer);
			delete typingUsers[no];
			document.getElementById(`typing-${no}`)?.remove();
		});
	}

	window.addEventListener("beforeunload", disconnectWebSocket);
});