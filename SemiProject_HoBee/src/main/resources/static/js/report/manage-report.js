const approveBtns = document.querySelectorAll(".approve-btn");
const rejectBtns = document.querySelectorAll(".reject-btn");

approveBtns.forEach(approveBtn => { approveBtn.addEventListener("click", statusUpdate) });
rejectBtns.forEach(rejectBtn => { rejectBtn.addEventListener("click", statusUpdate) });

function statusUpdate(e) {

  const reportNo = e.currentTarget.dataset.reportNo;
  let message = null;

  if(e.target.id == "APPROVED") {
    message = "신고가 승인되었습니다."
  } else {
    message = "신고가 거절되었습니다."
  }

  const obj = {
    "reportNo" : reportNo,
    "reportStatus" : e.target.id
  }

  fetch("/report/manageReport", {
    method : "PUT",
    headers : {"Content-Type" : "application/json"},
    body : JSON.stringify(obj)
  })
  .then(resp => resp.text())
  .then(result => {

    if(result > 0) {
      alert(message);
      selectReportList()
    } else {
      alert("신고 승인 및 거절에 실패했습니다...")
    }

  });

}

function selectReportList() {
  location.href = "/report/manageReport"
}

