document.addEventListener("DOMContentLoaded", () => {

  const joinBtn = document.querySelector("#joinBtn");
  const certBtn = document.querySelector("#certBtn");
  const certModal = document.querySelector("#certModal");
  const certModalClose = document.querySelector("#certModalClose");
  const certModalBackdrop = document.querySelector("#certModalBackdrop");
  const certSubmitBtn = document.querySelector("#certSubmitBtn");

  // 참여하기 버튼
  if (joinBtn) {
    joinBtn.addEventListener("click", async () => {
      if (!confirm("챌린지에 참여 하시겠습니까?")) return;
      const result = await fetch(`/challenge/join/${challengeNo}`, {
        method: "POST"
      }).then(r => r.json());

      alert(result.message);
      if (result.success) location.reload();
    });
  }

  // 인증하기 버튼 → 모달 열기
  if (certBtn) {
    certBtn.addEventListener("click", () => {
      certModal.classList.remove("popup-hidden");
    });
  }

  // 모달 닫기
  [certModalClose, certModalBackdrop].forEach(el => {
    el?.addEventListener("click", () => {
      certModal.classList.add("popup-hidden");
      document.querySelector("#certTitle").value = "";
      document.querySelector("#certContent").value = "";
    });
  });

  // 파일 선택 시 미리보기
  document.querySelector("#certImg")?.addEventListener("change", function () {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => {
      document.querySelector("#certImgThumb").src = e.target.result;
      document.querySelector("#certImgPreview").style.display = "flex";
    };
    reader.readAsDataURL(file);
  });

  // 이미지 제거 버튼
  document.querySelector("#certImgRemove")?.addEventListener("click", () => {
    document.querySelector("#certImg").value = "";
    document.querySelector("#certImgPreview").style.display = "none";
    document.querySelector("#certImgThumb").src = "";
  });

  // 인증 등록 버튼
  // 인증 등록 버튼 - 이미지 업로드 포함
  if (certSubmitBtn) {
    certSubmitBtn.addEventListener("click", async () => {
      const title = document.querySelector("#certTitle").value.trim();
      const content = document.querySelector("#certContent").value.trim();
      const imgFile = document.querySelector("#certImg").files[0];

      if (!title) { alert("인증 제목을 입력해주세요."); return; }
      if (!content) { alert("인증 내용을 입력해주세요."); return; }

      let imgUrl = "";

      // 이미지가 있으면 먼저 업로드
      if (imgFile) {
        const imgForm = new FormData();
        imgForm.append("file", imgFile);

        imgUrl = await fetch("/challenge/imageUpload", {
          method: "POST",
          body: imgForm
        }).then(r => r.text());
      }

      // 이미지 URL을 content에 포함시켜서 전송
      const finalContent = imgUrl
        ? content + `<br><img src="${imgUrl}" style="max-width:100%; margin-top:10px;">`
        : content;

      const formData = new FormData();
      formData.append("certTitle", title);
      formData.append("certContent", finalContent);

      const result = await fetch(`/challenge/certify/${challengeNo}`, {
        method: "POST",
        body: formData
      }).then(r => r.json());

      if (result.success) {
        certModal.classList.add("popup-hidden");
        if (result.complete) {
          alert("🎉 목표를 달성했습니다! 축하해요!");
        } else {
          alert(result.message);
        }
        location.reload();
      } else {
        alert(result.message);
      }
    });
  }

  const certItems = document.querySelectorAll("#certHistoryList .cert-item");
  const dateSet = [];
  let lastDate = null;

  certItems.forEach(item => {
    const rawDate = item.dataset.date;
    if (rawDate !== lastDate) {
      // 구분선 삽입
      const divider = document.createElement("div");
      divider.className = "cert-date-divider";
      divider.id = `cert-date-${rawDate}`;  // 앵커용 id
      divider.textContent = rawDate;
      item.before(divider);
      lastDate = rawDate;
      dateSet.push(rawDate);
    }
  });

  // 날짜가 2개 이상일 때만 네비게이터 표시
  const certHistory = document.querySelector(".cert-history");
  if (certHistory && dateSet.length >= 2) {
    const nav = document.createElement("div");
    nav.className = "cert-date-nav";

    const label = document.createElement("span");
    label.className = "cert-date-nav-label";
    label.textContent = "날짜 이동:";
    nav.appendChild(label);

    const navList = document.createElement("div");
    navList.className = "cert-date-nav-list";

    dateSet.forEach(date => {
      const btn = document.createElement("button");
      btn.className = "cert-date-nav-btn";
      btn.textContent = date;
      btn.addEventListener("click", () => {
        const target = document.querySelector(`#cert-date-${date}`);
        if (target) {
          target.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      });
      navList.appendChild(btn);
    });

    nav.appendChild(navList);

    // 인증 내역 섹션 맨 위에 네비게이터 삽입
    certHistory.insertBefore(nav, certHistory.querySelector("#certHistoryList"));
  }

  // 인증 이미지 클릭 시 원본 크기로 보기
  document.addEventListener("click", e => {
    const img = e.target.closest(".cert-item-content img");
    if (!img) return;

    const overlay = document.createElement("div");
    overlay.className = "img-overlay";

    const fullImg = document.createElement("img");
    fullImg.src = img.src;
    fullImg.className = "img-overlay-img";

    overlay.appendChild(fullImg);
    document.body.appendChild(overlay);

    overlay.addEventListener("click", () => overlay.remove());
  });

});