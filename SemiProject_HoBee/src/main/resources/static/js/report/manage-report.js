const approveBtns = document.querySelectorAll(".approve-btn");
const rejectBtns = document.querySelectorAll(".reject-btn");
const watchBtns = document.querySelectorAll(".watch-btn");
const reportContentModal = document.querySelector(".report-modal-overlay")
const contentBox = document.querySelector(".content-box");
const writer = document.querySelector(".writer");
const modalClose = document.querySelector(".modal-close");

approveBtns.forEach(approveBtn => { approveBtn.addEventListener("click", statusUpdate) });
rejectBtns.forEach(rejectBtn => { rejectBtn.addEventListener("click", statusUpdate) });

function statusUpdate(e) {

  const reportNo = e.currentTarget.dataset.reportNo;
  let message = null;

  if (e.target.id == "APPROVED") {
    message = "신고가 승인되었습니다."
  } else {
    message = "신고가 거절되었습니다."
  }

  const obj = {
    "reportNo": reportNo,
    "reportStatus": e.target.id
  }

  fetch("/report/manageReport", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(obj)
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {
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

watchBtns.forEach(watchBtn => {

  watchBtn.addEventListener("click", e => {

    const reportNo = e.currentTarget.dataset.reportNo

    fetch("/report/selectTarget?reportNo=" + reportNo)
      .then(resp => resp.json())
      .then(writing => {
        writer.innerText = "작성자 : " + writing.reportedNickname;
        contentBox.innerHTML = writing.targetContent;
      });
    reportContentModal.classList.remove("popup-hidden");
  });
});

modalClose.addEventListener("click", () => {
  reportContentModal.classList.add("popup-hidden");
});