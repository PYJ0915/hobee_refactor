const insertBtn = document.querySelector("#insertBtn");
const myBoardBtn = document.querySelector("#myBoardBtn");

if (insertBtn != null) {
    insertBtn.addEventListener('click', () => {
        // 클릭된 버튼에서 해당 게시판의 경로를 읽어옴
        const boardPath = insertBtn.getAttribute("data-board-path");
        
        // 최종 URL 생성: /editBoard/notice/insert 또는 /editBoard/hobby/1/insert 등
        location.href = `/${boardPath}/insert`;
    });
}

if (myBoardBtn != null) {
    myBoardBtn.addEventListener('click', () => {

        const boardPath = myBoardBtn.getAttribute("data-board-path");
        const target = "/myBoard";

        if(boardPath.includes(target)){
            return;
        }

        location.href = `/${boardPath}/myBoard`;
    })
}


// 현재 URL에서 sort, dir 파라미터 읽기
const params = new URLSearchParams(location.search);
const currentSort = params.get("sort") || "default";
const currentDir = params.get("dir") || "desc";

// 화살 아이콘 표시 함수
function updateSortIcons() {
	
	document.querySelectorAll("th[data-sort]").forEach(th => {
		const icon = th.querySelector(".sort-icon");
		if(th.dataset.sort == currentSort) {
			icon.textContent = currentDir === "desc" ? " ▼" : " ▲";
			th.classList.add("active-sort")
		} else {
			icon.textContent = " ↕";
			th.classList.remove("active-sort");
		}
	});
}

// 정렬 클릭 처리
function handleSort(sortKey) {

    // 같은 컬럼 클릭 시 방향 토글, 다른 컬럼 클릭 시 desc로 초기화
    const newDir = (currentSort === sortKey && currentDir === "desc") ? "asc" : "desc";

    let path = `/board/list/${boardCode}`;
    if (categoryCode != null) path += `/${categoryCode}`;

    // 검색 파라미터 유지
    const key   = params.get("key");
    const query = params.get("query");
    const cp    = params.get("cp") || "1";

    const newParams = new URLSearchParams();
    newParams.set("sort", sortKey);
    newParams.set("dir", newDir);
    newParams.set("cp", cp);

    if (key)   newParams.set("key", key);
    if (query) newParams.set("query", query);

    location.href = path + "?" + newParams.toString();
}

// 이벤트 등록
document.querySelectorAll("th[data-sort]").forEach(th => {
    th.addEventListener("click", () => handleSort(th.dataset.sort));
});

// 페이지 로드 시 아이콘 업데이트
updateSortIcons();