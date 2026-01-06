const releaseBtns = document.querySelectorAll(".action-btn")

releaseBtns.forEach(releaseBtn => {

  releaseBtn.addEventListener("click", e => {

    let memberNickname = e.currentTarget.dataset.memberNickname;
    const penaltyNo = e.currentTarget.dataset.penaltyNo;
    const penaltyType = e.currentTarget.dataset.penaltyType;

    let message = null;

    if(penaltyType == 'SUSPEND') message = memberNickname + "님의 계정 이용 제한을 해제하시겠습니까?";
    else message = memberNickname + "님은 영구정지 회원입니다. 정말 계정 이용 제한을 해제하시겠습니까?"

    if(!confirm(message)) {
      alert(memberNickname + "님의 계정 이용 제한 해제가 취소되었습니다.")
      return;
    }

    fetch("/penalty/managePenalty", {
      method : "POST", 
      headers : {"Content-Type" : "application/json"},
      body : JSON.stringify(penaltyNo)
    })
    .then(resp => resp.text())
    .then(result => {

      if(result > 0) {
        alert(memberNickname + "님의 계정 이용 제한이 해제되었습니다.")
        selectPenaltyList()
      } else {
        alert(memberNickname + "님의 계정 이용 제한 해제에 실패했습니다.")
      }

    });

  });

});

function selectPenaltyList() {
  location.href = "/penalty/managePenalty"
}