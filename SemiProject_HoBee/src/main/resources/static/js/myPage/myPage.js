/**  회원 정보 수정 페이지 **/

const updateInfo = document.querySelector("#updateInfo"); // form 태그

// #updateInfo 요소가 존재 할 때만 수행
if(updateInfo != null) {

  // 주소 검색 버튼 클릭 시
  document.querySelector(".address-btn").addEventListener("click", execDaumPostcode)

  // 다음 주소 API
  function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
        // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

        // 각 주소의 노출 규칙에 따라 주소를 조합한다.
        // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
        var addr = ''; // 주소 변수

        //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
        if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
            addr = data.roadAddress;
        } else { // 사용자가 지번 주소를 선택했을 경우(J)
            addr = data.jibunAddress;
        }

        // 우편번호와 주소 정보를 해당 필드에 넣는다.
        document.getElementById('postcode').value = data.zonecode;
        document.getElementById("address").value = addr;
        // 커서를 상세주소 필드로 이동한다.
        document.getElementById("detailAddress").focus();
        }
        
    }).open();
}




  // form 태그 제출된 경우
  updateInfo.addEventListener("submit", e => {

    const memberNickname = document.querySelector("#memberNickname");
    const memberTel = document.querySelector("#memberTel");
    const memberAddress = document.querySelectorAll("[name='memberAddress']");

    /* 닉네임 유효성 검사 */
    
    // 미입력
    if(memberNickname.value.trim().length === 0) {
      alert("닉네임을 입력해주세요");
      e.preventDefault(); // 제출 막기
      return;
    }

    // 닉네임 정규식에 맞지 않는 경우
    let regExp = /^[가-힣\w\d]{2,10}$/;

    if( !regExp.test(memberNickname.value)) {
      alert("한글,영어,숫자 2~10글자로 구성해주세요.");
      e.preventDefault(); // 제출 막기
      return;
    }

    // 닉네임 중복 검사
    fetch("/member/checkNickname?memberNickname=" + memberNickname.value)
    .then(resp => resp.text())
    .then(count => {
        
        if(count == 1) { // 중복인 경우
            alert("중복되는 닉네임입니다. 다른 닉네임을 입력해주세요.");
            e.preventDefault();
            return;
        }

    })    

    /* 전화번호 유효성 검사 */

    // 미입력
    if(memberTel.value.trim().length === 0) {
      alert("전화번호를 입력해 주세요");
      e.preventDefault();
      return;
    }    

    // 전화번호가 정규식에 맞지 않는 경우(01*-***(*)-****)
    regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
    
    if(!regExp.test(memberTel.value)) {
      alert("전화번호가 유효하지 않습니다");
      e.preventDefault();
      return;
    }


    /* 주소 유효성 검사 */
        
    // 입력을 안하면 전부 안하고, 입력하면 전부 해야함

    const addr0 = memberAddress[0].value.trim().length == 0; // t/f
    const addr1 = memberAddress[1].value.trim().length == 0; // t/f
    const addr2 = memberAddress[2].value.trim().length == 0; // t/f

    // 모두 true 인 경우만 true 저장
    const result1 = addr0 && addr1 && addr2; // 아무것도 입력하지 않은 경우

    // 모두 false 인 경우만 true 저장
    const result2 = !(addr0 || addr1 || addr2); // 전부 입력한 경우

    // 모두 입력 또는 모두 미입력이 아니면 (=> 유효하지 않은 경우)
    if( !(result1 || result2) ) {
      alert("주소를 모두 작성 또는 미작성 해주세요");
      e.preventDefault();
    }

  });  

}


/** 비밀번호 수정 **/

// 비밀번호 변경 form 태그
const changePw = document.querySelector("#changePw");

if(changePw != null) { // 제출 되었을 때(변경 버튼 클릭)
    
  changePw.addEventListener("submit", e => {

    const currentPw = document.querySelector("#currentPw");
    const newPw = document.querySelector("#newPw");
    const confirmPw = document.querySelector("#confirmPw");

    // - 값을 모두 입력했는가

    let str; // undefined 상태
    if( currentPw.value.trim().length === 0 ) str = "현재 비밀번호를 입력해주세요";
    else if( newPw.value.trim().length === 0 ) str = "새 비밀번호를 입력해주세요";
    else if( confirmPw.value.trim().length === 0 ) str = "새 비밀번호 확인을 입력해주세요";

    if(str != undefined) { // str에 값이 대입됨 == if 중 하나 실행됨
      alert(str);
      e.preventDefault();
      return;
    }

    // 새 비밀번호 정규식
    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

    if( !regExp.test(newPw.value) ) {
      alert("새 비밀번호가 유효하지 않습니다");
      e.preventDefault();
      return;
    }

    // 새 비밀번호 == 새 비밀번호 확인
    if( newPw.value != confirmPw.value ) {
      alert("새 비밀번호가 일치하지 않습니다");
      e.preventDefault();
      return;
    } 
  });
};