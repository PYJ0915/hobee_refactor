const insertBtn = document.querySelector("#insertBtn");

if (insertBtn != null) {
    insertBtn.addEventListener('click', () => {
        // 클릭된 버튼에서 해당 게시판의 경로를 읽어옴
        const boardPath = insertBtn.getAttribute("data-board-path");
        
        // 최종 URL 생성: /editBoard/notice/insert 또는 /editBoard/hobby/1/insert 등
        location.href = `/editBoard/${boardPath}/insert`;
    });
}