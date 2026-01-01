const boardReportBtn = document.querySelector("#board-report");
const commentReportBtn = document.querySelector("#comment-report");
const reportModal = document.querySelector("#reportModal");
const reportWriter = document.querySelector("#reportWriter");
const reportContent = document.querySelector("#reportContent");

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

  reportWriter.innerText = writer;

  reportModal.classList.remove("popup-hidden");

};

document.querySelector(".submit-report-btn").addEventListener("click", () => {

  const checkedReason = document.querySelector('input[name="reason"]:checked');
  const reportDetail = document.querySelector(".detail-textarea").value;
  const reportedMemberNickname = reportWriter.innerText;  
  const reportedMemberNo = getMemberNo(reportedMemberNickname);
  

  if(checkedReason == null) {
    alert("신고 사유를 선택해 주세요!")
    return;
  }

  const obj = {
    "reportReason" : checkedReason.value,
    "reportDetail" : reportDetail == "" ? null : reportDetail,
    "reporterMemberNo" : loginMemberNo,
    "reportedMemberNo" : reportedMemberNo 
  };

});
