/* =============================================
   1. 섹션 아코디언 (기존 동작 방식 그대로 유지)
   ============================================= */
document.querySelectorAll(".accordion-header").forEach(btn => {
    btn.addEventListener("click", () => {
        const body = btn.nextElementSibling;
        const icon = btn.querySelector(".accordion-icon");

        body.classList.toggle("hidden");
        icon.classList.toggle("open");
    });
});

/* =============================================
   2. 챌린지별 인증 내역 토글 (클릭 시 API 조회)
   ============================================= */
document.querySelectorAll(".challenge-item-row").forEach(row => {

    row.addEventListener("click", async () => {
        const challengeNo = row.dataset.challengeNo;
        const memberNo    = row.dataset.memberNo || loginMemberNo;
        const historyDiv  = document.querySelector(`#certHistory-${challengeNo}`);
        const icon        = row.querySelector(".cert-toggle-icon");

        const isHidden = historyDiv.classList.contains("popup-hidden");

        if (isHidden) {
            historyDiv.classList.remove("popup-hidden");
            icon.textContent = "▼";

            if (historyDiv.dataset.loaded) return;

            try {
                const certList = await fetch(
                    `/challenge/certList?challengeNo=${challengeNo}&memberNo=${memberNo}`
                ).then(r => r.json());

                historyDiv.dataset.loaded = "true";

                if (!certList || certList.length === 0) {
                    historyDiv.innerHTML =
                        '<p style="font-size:13px; color:#aaa; padding:8px 0;">아직 인증 내역이 없습니다.</p>';
                    return;
                }

                historyDiv.innerHTML = certList.map(cert => `
                    <div class="cert-inline-item">
                        <div class="cert-inline-header">
                            <span class="cert-inline-title">${escapeHtml(cert.certTitle)}</span>
                            <span class="cert-inline-date">${cert.certWriteDate}</span>
                        </div>
                        <div class="cert-inline-content">${cert.certContent}</div>
                    </div>
                `).join("");

            } catch (err) {
                historyDiv.innerHTML =
                    '<p style="font-size:13px; color:#e84545; padding:8px 0;">불러오기 실패</p>';
            }

        } else {
            historyDiv.classList.add("popup-hidden");
            icon.textContent = "▲";
        }
    });
});

function escapeHtml(str) {
    if (!str) return "";
    return str
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}