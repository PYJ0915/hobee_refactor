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
};