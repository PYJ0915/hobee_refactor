// 오늘 날짜를 시작일 최솟값으로 설정
    const today = new Date().toISOString().split("T")[0];
    document.querySelector("#startDate").min = today;
    document.querySelector("#endDate").min   = today;

    // 시작일 변경 시 종료일 최솟값 업데이트
    document.querySelector("#startDate").addEventListener("change", function () {
        const endInput = document.querySelector("#endDate");
        endInput.min = this.value;

        // 종료일이 시작일보다 앞이면 초기화
        if (endInput.value && endInput.value < this.value) {
            endInput.value = "";
        }
        updateDateHint();
    });

    document.querySelector("#endDate").addEventListener("change", updateDateHint);

    // 기간 계산 힌트
    function updateDateHint() {
        const start = document.querySelector("#startDate").value;
        const end   = document.querySelector("#endDate").value;
        const hint  = document.querySelector("#dateHint");

        if (start && end) {
            const diff = Math.ceil(
                (new Date(end) - new Date(start)) / (1000 * 60 * 60 * 24)
            ) + 1;
            hint.textContent = `총 ${diff}일간 진행되는 챌린지입니다.`;
            hint.style.color = "#2d7a3a";
        } else {
            hint.textContent = "";
        }
    }

    // 폼 유효성 검사
    document.querySelector("#challengeForm").addEventListener("submit", function (e) {
        const title   = document.querySelector("#challengeTitle").value.trim();
        const desc    = document.querySelector("#challengeDesc").value.trim();
        const type    = document.querySelector("#challengeType").value;
        const goal    = parseInt(document.querySelector("#goalCount").value);
        const start   = document.querySelector("#startDate").value;
        const end     = document.querySelector("#endDate").value;

        if (!title) {
            alert("챌린지 제목을 입력해주세요.");
            document.querySelector("#challengeTitle").focus();
            e.preventDefault(); return;
        }
        if (!desc) {
            alert("챌린지 설명을 입력해주세요.");
            document.querySelector("#challengeDesc").focus();
            e.preventDefault(); return;
        }
        if (!type) {
            alert("카테고리를 선택해주세요.");
            document.querySelector("#challengeType").focus();
            e.preventDefault(); return;
        }
        if (!goal || goal < 1 || goal > 365) {
            alert("목표 인증 횟수를 1~365 사이로 입력해주세요.");
            document.querySelector("#goalCount").focus();
            e.preventDefault(); return;
        }
        if (!start || !end) {
            alert("챌린지 기간을 입력해주세요.");
            e.preventDefault(); return;
        }
        if (end < start) {
            alert("종료일은 시작일 이후여야 합니다.");
            document.querySelector("#endDate").focus();
            e.preventDefault(); return;
        }
    });