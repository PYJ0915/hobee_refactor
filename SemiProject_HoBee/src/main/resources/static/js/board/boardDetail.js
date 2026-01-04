const boardReportBtn = document.querySelector("#board-report");
const commentReportBtn = document.querySelector("#comment-report");
const reportModal = document.querySelector("#reportModal");
const reportWriter = document.querySelector("#reportWriter");
const reportContent = document.querySelector("#reportContent");
let reportedMemberNo = null;

let targetType = null;
let targetNo = null;

// 로그인한 회원의 회원번호
// 버튼이 존재할 때만 처리를 해줌으로써 공지게시판에서의 오류 제거
if (boardReportBtn != null) {
    const loginMemberNo = boardReportBtn.dataset.loginMemberNo;
    boardReportBtn.addEventListener("click", showReport);
}
// commentReportBtn.addEventListener("click", showReport);



document.querySelector(".close-btn").addEventListener("click", () => {
   reportModal.classList.add("popup-hidden"); 
});

function showReport(e) {

  if(loginMemberNo == 0) {
    
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


const updateBtn = document.querySelector("#updateBtn");

if (updateBtn != null) { // 수정 버튼 존재 시

  updateBtn.addEventListener("click", () => {

    location.href = "/editBoard" 
    + location.pathname 
    + "/update" 
    + location.search;
    
  });

}