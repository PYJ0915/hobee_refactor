const likeBtn = document.querySelector(".like-btn");
const boardReportBtn = document.querySelector("#board-report");
const commentReportBtn = document.querySelector("#comment-report");
const reportModal = document.querySelector("#reportModal");
const reportWriter = document.querySelector("#reportWriter");
const reportContent = document.querySelector("#reportContent");
let reportedMemberNo = null;

let targetType = null;
let targetNo = null;

likeBtn.addEventListener("click", () => {

if(loginMemberNo == null) {
    
    if(!confirm("로그인이 필요한 기능입니다. 로그인하시겠습니까?")) return;
      
    location.href="/member/loginPage"
    return;

  }

  let url = null;

  switch(boardCode) {
    case 1:
      url = "/notice/like";
      break;
    case 2:
      url = "/hobby/like";
      break;
    case 3:
      url = "/free/like";
      break;
  }

  const obj = {
    "memberNo" : loginMemberNo,
    "boardNo" : boardNo,
    "likeCheck" : likeCheck
  }

  fetch(url, {
    method : "POST",
    headers : {"Content-Type" : "application/json"},
    body : JSON.stringify(obj)
  })
  .then(resp => resp.text())
  .then(count => {

    if(count == -1) {
      console.log("좋아요 처리 실패");
      return;
    }

    const boardLike = document.querySelector(".boardLike");

    // 5. likeCheck 값 0 <-> 1 변환
    // => 클릭될 때 마다 INSERT/DELETE 동작을 번갈아가면서 할 수 있게끔
    likeCheck = likeCheck == 0 ? 1 : 0;

    // 6. 좋아요 아이콘 채우기/비우기 변환
    boardLike.classList.toggle("fa-regular");
    boardLike.classList.toggle("fa-solid");

    likeBtn.classList.toggle("like-checked");
   
    // 7. 게시글 좋아요 수 수정
    document.querySelector(".like-count").innerText = count;

  });

});

// 버튼이 존재할 때만 처리를 해줌으로써 공지게시판에서의 오류 제거
if(boardReportBtn != null) {
  boardReportBtn.addEventListener("click", showReport);
}

if(commentReportBtn != null) {
  commentReportBtn.addEventListener("click", showReport);
}
document.querySelector(".close-btn").addEventListener("click", () => {
   reportModal.classList.add("popup-hidden"); 
});

function showReport(e) {

  if(loginMemberNo == null) {
    
    if(!confirm("로그인이 필요한 기능입니다. 로그인하시겠습니까?")) return;
      
    location.href="/member/loginPage"
    return;

  }

  const writer = e.currentTarget.dataset.writer;

  // 신고당한 회원의 번호 얻어오기
  reportedMemberNo = e.currentTarget.dataset.reportedMemberNo;

  reportWriter.innerText = writer;

  targetType = e.currentTarget.id == "board-report" ? "B" : "C";
  targetNo = e.currentTarget.dataset.targetNo;
  
  reportModal.classList.remove("popup-hidden");

};

document.querySelector(".submit-report-btn").addEventListener("click", () => {

  const checkedReason = document.querySelector('input[name="reason"]:checked');
  let reportDetail = document.querySelector(".detail-textarea");
  const nickname = reportWriter.innerText;

  if(checkedReason == null) {
    alert("신고 사유를 선택해 주세요!")
    return;
  }

  if(reportedMemberNo == loginMemberNo) {
    alert("본인 계정은 신고할 수 없습니다.")
    reportDetail.value = "";
    checkedReason.checked = false;
    reportModal.classList.add("popup-hidden");
    return;
  }

  if(authorLevel == 2) {
    alert("관리자 계정은 신고할 수 없습니다.")
    reportDetail.value = "";
    checkedReason.checked = false;
    reportModal.classList.add("popup-hidden");
    return;
  }

  const obj = {
    "reportReason" : checkedReason.value,
    "reportDetail" : reportDetail.value == "" ? null : reportDetail.value,
    "reporterMemberNo" : loginMemberNo,
    "reportedMemberNo" : reportedMemberNo,
    "targetType" : targetType,
    "targetNo" : targetNo
  };  

  console.log(obj);

  fetch("/report/insertReport", {
    method : "POST",
    headers : {"Content-Type" : "application/json"},
    body : JSON.stringify(obj)
  })
  .then(resp => resp.text())
  .then(result => {
    if(result > 0) {
      alert(nickname + "님이 신고되었습니다.");
      reportModal.classList.add("popup-hidden");
      reportDetail.value = "";
      checkedReason.checked = false;
    } else {
      alert("신고에 실패했습니다. 다시 시도해주세요.");
    }
  });
});

const deleteBtn = document.querySelector("#deleteBtn");

if (deleteBtn != null) {
  deleteBtn.addEventListener("click", () => {

    if (!confirm("삭제 하시겠습니까?")) {
      alert("취소됨")
      return;
    }

    const url = "/editBoard" + location.pathname + "/delete";

    // form태그 생성
    const form = document.createElement("form");
    form.action = url;
    form.method = "POST";

    // cp값을 저장할 input 생성
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "cp";

    // 쿼리스트링에서 원하는 파라미터 얻어오기
    const params = new URLSearchParams(location.search)
    const cp = params.get("cp");
    input.value = cp;

    form.append(input);

    // 화면에 form태그를 추가한 후 제출하기
    document.querySelector("body").append(form);
    form.submit();
  });
}

<<<<<<< HEAD

const updateBtn = document.querySelector("#updateBtn");

if (updateBtn != null) { // 수정 버튼 존재 시

  updateBtn.addEventListener("click", () => {

    location.href = "/editBoard" 
    + location.pathname 
    + "/update" 
    + location.search;
    
  });

}
=======
>>>>>>> 66494b1849aaaa4e07fe43215abf1aaea22354d0
