const followBtn = document.querySelector("#followBtn");
const followerStat  = document.querySelector("#followerStat");
const followingStat = document.querySelector("#followingStat");
const followModal   = document.querySelector("#followModal");
const followModalTitle = document.querySelector("#followModalTitle");
const followModalList  = document.querySelector("#followModalList");
const followModalClose = document.querySelector("#followModalClose");
const followModalBackdrop = document.querySelector("#followModalBackdrop");

if (followBtn != null) {

    followBtn.addEventListener("click", async () => {

        if (loginMemberNo == null) {
            if (!confirm("로그인이 필요한 기능입니다. 로그인하시겠습니까?")) return;
            location.href = "/member/loginPage";
            return;
        }

        const result = await fetch("/follow/toggle", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ followingNo: targetMemberNo })
        }).then(resp => resp.json());

        // 버튼 UI 업데이트
        const btnText = followBtn.querySelector("span");

        if (result.isFollowing) {
            btnText.textContent = "팔로잉 ✓";
            followBtn.classList.add("following");
        } else {
            btnText.textContent = "팔로우";
            followBtn.classList.remove("following");
        }

        // 팔로워 수 업데이트
        const followerCountEl = document.querySelector("#followerCount");
        if (followerCountEl) {
            followerCountEl.textContent = result.followerCount;
        }
    });
}

if (followerStat != null) {

    followerStat.addEventListener("click", () => {
        openFollowModal("팔로워", "followers");
    });

    followingStat.addEventListener("click", () => {
        openFollowModal("팔로잉", "followings");
    });

    followModalClose.addEventListener("click", closeFollowModal);
    followModalBackdrop.addEventListener("click", closeFollowModal);
}

async function openFollowModal(title, type) {
    followModalTitle.textContent = title;
    followModalList.innerHTML = '<li class="follow-modal-empty">불러오는 중...</li>';
    followModal.classList.remove("popup-hidden");

    const list = await fetch(`/follow/${type}/${isMyProfile ? loginMemberNo : targetMemberNo}`)
        .then(resp => resp.json());

    followModalList.innerHTML = "";

    if (list.length === 0) {
        followModalList.innerHTML = `<li class="follow-modal-empty">${title} 목록이 없습니다.</li>`;
        return;
    }

    const defaultImg = "/images/user.png";

    list.forEach(follow => {
        const memberNo   = type === "followers" ? follow.followerNo : follow.followingNo;
        const li = document.createElement("li");
        li.className = "follow-modal-item";
        li.innerHTML = `
            <a href="/member/profile/${memberNo}" class="follow-modal-link">
                <img src="${follow.profileImg || defaultImg}"
                     class="follow-modal-img" alt="프로필">
                <span class="follow-modal-name">${follow.memberNickname}</span>
            </a>
        `;
        followModalList.appendChild(li);
    });
}

function closeFollowModal() {
    followModal.classList.add("popup-hidden");
}