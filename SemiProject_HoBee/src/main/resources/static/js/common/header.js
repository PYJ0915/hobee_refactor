// 알림 관련 JS
const notiBtn = document.querySelector("#notiBtn");
const notiDropdown = document.querySelector("#notiDropdown");
const notiBadge = document.querySelector("#notiBadge");
const notiList = document.querySelector("#notiList");
const notiReadAll = document.querySelector("#notiReadAll");
const notiDeleteAll = document.querySelector("#notiDeleteAll");

// 로그인 상태일 때만 실행
if (notiBtn != null) {

	// 페이지 로드 시 읽지 않은 알림 수 조회
	loadUnreadCount();

	// 알림 버튼 클릭 → 드롭다운 토글
	notiBtn.addEventListener("click", async (e) => {
		e.stopPropagation();

		const isHidden = notiDropdown.classList.contains("popup-hidden");

		if (isHidden) {
			notiDropdown.classList.remove("popup-hidden");
			await loadNotifications(); // 열 때 목록 조회
		} else {
			notiDropdown.classList.add("popup-hidden");
		}
	});

	// 드롭다운 외부 클릭 시 닫기
	document.addEventListener("click", (e) => {
		if (!notiBtn.contains(e.target) && !notiDropdown.contains(e.target)) {
			notiDropdown.classList.add("popup-hidden");
		}
	});

	// 모두 읽음 버튼
	if (notiReadAll != null) {
		notiReadAll.addEventListener("click", async () => {
			await fetch("/notification/readAll", { method: "PUT" });
			notiBadge.style.display = "none";
			notiBtn.querySelector("i").className = "fa-regular fa-bell";
			document.querySelectorAll(".noti-item.unread")
				.forEach(el => el.classList.remove("unread"));
		});
	}
}

// 읽지 않은 알림 수 조회
async function loadUnreadCount() {
	try {
		const resp = await fetch("/notification/unreadCount");

		// 비로그인 등으로 실패 시 조용히 종료
		if (!resp.ok) return;

		const count = await resp.text();

		if (count > 0) {
			notiBadge.textContent = count > 99 ? "99+" : count;
			notiBadge.style.display = "flex";
			notiBtn.querySelector("i").className = "fa-solid fa-bell";
		} else {
			notiBadge.style.display = "none";
			notiBtn.querySelector("i").className = "fa-regular fa-bell";
		}
	} catch (err) {
		console.log("알림 수 조회 실패:", err);
	}
}

// 알림 목록 조회
async function loadNotifications() {

	const notifications = await fetch("/notification/list")
		.then(resp => resp.json());

	notiList.innerHTML = "";

	if (notifications.length === 0) {
		notiList.innerHTML = '<li class="noti-empty">알림이 없습니다.</li>';
		return;
	}

	notifications.forEach(noti => {

		const li = document.createElement("li");
		li.className = "noti-item" + (noti.notiReadFl === "N" ? " unread" : "");

		const defaultImg = "/images/user.png";
		const imgSrc = noti.senderProfileImg || defaultImg;

		li.innerHTML = `
		        <img src="${imgSrc}" class="noti-profile-img" alt="프로필">
		        <div class="noti-content">
		            <p class="noti-msg">
		                <strong>${noti.senderNickname}</strong>${noti.notiMessage}
		            </p>
		            <p class="noti-date">${noti.notiDate}</p>
		        </div>
		        <button class="noti-delete-btn" data-noti-no="${noti.notiNo}">✕</button>
		    `;

		// 개별 삭제 버튼
		li.querySelector(".noti-delete-btn").addEventListener("click", async (e) => {
			e.stopPropagation(); // 클릭 이벤트 버블링 방지 (li 클릭 이벤트 막기)
			await fetch(`/notification/delete/${noti.notiNo}`, { method: "DELETE" });
			li.remove();

			// 알림 없으면 empty 표시
			if (notiList.querySelectorAll(".noti-item").length === 0) {
				notiList.innerHTML = '<li class="noti-empty">알림이 없습니다.</li>';
				notiBtn.querySelector("i").className = "fa-regular fa-bell";
			}

			await loadUnreadCount();
		});

		// 클릭 시 읽음 처리 + 이동
		li.addEventListener("click", async () => {
			if (noti.notiReadFl === "N") {
				await fetch(`/notification/read/${noti.notiNo}`, { method: "PUT" });
				li.classList.remove("unread");
				await loadUnreadCount();
			}

			// FOLLOW면 해당 프로필로 이동
			if (noti.notiType === "FOLLOW") {
				location.href = `/member/profile/${noti.notiTargetNo}`;
			} else if (noti.notiType === "BOARD" || noti.notiType === "COMMENT") {
				const boardNo = noti.notiTargetNo;
				const boardCode = await fetch(`/board/getBoardCode/${boardNo}`)
					.then(resp => resp.text());
				location.href = `/board/detail/${boardCode}/${boardNo}`;
			} else if (noti.notiType === "GATHERING") {
				// gatheringNo → boardNo 조회 후 모임 게시글(boardCode=4)로 이동
				const boardNo = await fetch(`/gathering/getBoardNo/${noti.notiTargetNo}`)
					.then(resp => resp.text());
				location.href = `/board/detail/4/${boardNo}`;
			}


		});

		notiList.appendChild(li);
	});
}

if (notiDeleteAll != null) {
	notiDeleteAll.addEventListener("click", async () => {
		if (!confirm("알림을 모두 삭제하시겠습니까?")) return;
		await fetch("/notification/deleteAll", { method: "DELETE" });
		notiList.innerHTML = '<li class="noti-empty">알림이 없습니다.</li>';
		notiBadge.style.display = "none";
		notiBtn.querySelector("i").className = "fa-regular fa-bell"; // 4번과 연동
	});
}

const chatBadge = document.querySelector("#chatBadge");

async function loadUnreadChatCount() {
	try {
		const resp = await fetch("/chat/unreadCount");
		if (!resp.ok) return;
		const count = await resp.text();

		const chatIcon = document.querySelector("#chatIconBtn i"); // 추가

		if (count > 0) {
			chatBadge.textContent = count > 99 ? "99+" : count;
			chatBadge.style.display = "flex";
			if (chatIcon) chatIcon.className = "fa-solid fa-comment-dots"; // 추가
		} else {
			chatBadge.style.display = "none";
			if (chatIcon) chatIcon.className = "fa-regular fa-comment-dots"; // 추가
		}
	} catch (err) {
		console.log("채팅 수 조회 실패:", err);
	}
}

// notiBtn이 있을 때만 (로그인 상태) 실행
if (notiBtn != null) {
	loadUnreadCount();
	loadUnreadChatCount(); // 추가
}

