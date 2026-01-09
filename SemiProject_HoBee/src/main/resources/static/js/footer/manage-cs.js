const watchBtns = document.querySelectorAll(".watch-btn");
const completeBtns = document.querySelectorAll(".complete-btn")
const csModal = document.querySelector(".cs-modal-overlay")
const modalClose = document.querySelector(".modal-close");
const writer = document.querySelector(".writer");
const email = document.querySelector(".email");
const date = document.querySelector(".date");
const content = document.querySelector(".cs-content");


watchBtns.forEach(watchBtn => {

  watchBtn.addEventListener("click", e => {

    const csNo = e.currentTarget.dataset.csNo;

    fetch("/footer/selectTarget?csNo=" + csNo)
      .then(resp => resp.json())
      .then(cs => {
        writer.innerText = "작성자 : " + cs.csWriterName;
        email.innerText = "이메일 : " + cs.csWriterEmail;
        date.innerText = "문의일 : " + cs.csWriteDate;
        content.innerHTML = cs.csContent;
      });

    csModal.classList.remove("popup-hidden");
  });
});

modalClose.addEventListener("click", () => {
  csModal.classList.add("popup-hidden");
});

completeBtns.forEach(completeBtn => {

  completeBtn.addEventListener("click", e => {

    if(!confirm("해당 문의를 완료 처리하시겠습니까?")) {
      return;
    }

    const csNo = e.currentTarget.dataset.csNo;

    fetch("/footer/csComplete", {
      method : "PUT",
      headers : {"Content-Type" : "application/json"},
      body : JSON.stringify(csNo)
    })
    .then(resp => resp.text())
    .then(result => {

      if(result > 0) {
        alert("문의가 완료 처리되었습니다.");
        selectCSList();
      } else {
        alert("문의 완료 처리에 실패했습니다. 다시 시도해 주세요.")
      }

    });
  })
});

function selectCSList() {
  location.href = "/footer/manageCS"
}