const boardReportBtn = document.querySelector("#board-report");
const commentReportBtn = document.querySelector("#comment-report");
const reportModal = document.querySelector("#reportModal");
const reportWriter = document.querySelector("#reportWriter");
const reportContent = document.querySelector("#reportContent");
let reportedMemberNo = null;

// 로그인한 회원의 회원번호
const loginMemberNo = boardReportBtn.dataset.loginMemberNo;

boardReportBtn.addEventListener("click", showReport);
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
    "reportedMemberNo" : reportedMemberNo
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

