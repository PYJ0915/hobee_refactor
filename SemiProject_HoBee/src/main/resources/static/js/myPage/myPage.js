/**  회원 정보 수정 페이지 **/

const updateInfo = document.querySelector("#updateInfo"); // form 태그
if (!updateInfo) return; 

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

  // 서버에서 내려준 기존 취미 코드 (th:inline="javascript")
  const selectedHobbyList = window.selectedHobbyList || [];

  const subHobbyData = {
    "sports": [
        {id: 101, name: "러닝"}, {id: 102, name: "헬스"}, {id: 103, name: "등산"},
        {id: 104, name: "사이클"}, {id: 105, name: "볼링"}, {id: 106, name: "탁구"},
        {id: 107, name: "수영"}, {id: 108, name: "축구"}, {id: 109, name: "야구"}, {id: 110, name: "골프"}
    ],
    "art": [
        {id: 201, name: "드로잉"}, {id: 202, name: "캘리그라피"}, {id: 203, name: "사진 촬영"},
        {id: 204, name: "영상 편집"}, {id: 205, name: "악기 연주"}, {id: 206, name: "도예/공예"},
        {id: 207, name: "전시회·공연 관람"}
    ],
    "selfDevelop": [
        {id: 301, name: "독서"}, {id: 302, name: "글쓰기"}, {id: 303, name: "코딩"},
        {id: 304, name: "외국어 공부"}, {id: 305, name: "요리"}, {id: 306, name: "재테크/투자"}
    ],
    "social": [
        {id: 401, name: "봉사활동"}, {id: 402, name: "북클럽"}, {id: 403, name: "보드게임 모임"},
        {id: 404, name: "취미 클래스"}, {id: 405, name: "스포츠 동호회"}, {id: 406, name: "스터디 모임"},
        {id: 407, name: "그룹 운동"}
    ],
    "shopping": [
        {id: 501, name: "피규어/굿즈 수집"}, {id: 502, name: "음반/LP 수집"}, {id: 503, name: "향수 수집"},
        {id: 504, name: "패션 아이템 수집"}, {id: 505, name: "문구류 수집"}, {id: 506, name: "한정판/콜라보 수집"}
    ]
  };

  const activeMainHobbySet = new Set();

  const mainCategoryArea = document.querySelector("#mainCategoryArea");

  function getMainHobbyName(key) {
    const map = {
      art: "문화·예술",
      selfDevelop: "자기계발",
      sports: "운동·레저",
      social: "사회 교류",
      shopping: "수집·소비"
    };
    return map[key] || key;
  }

  function renderMainHobbies() {
    mainCategoryArea.innerHTML = "";

    Object.keys(subHobbyData).forEach(key => {
      const isActive = activeMainHobbySet.has(key);

      const div = document.createElement("div");
      div.className = "main-hobby-item";

      div.innerHTML = `
        <label class="radio-like ${isActive ? "checked" : ""}">
          <input type="radio" hidden>
          ${getMainHobbyName(key)}
        </label>
        ${
          isActive
            ? `<button type="button" class="remove-btn" data-key="${key}">✕</button>`
            : ""
        }
      `;

      // 메인 취미 선택
      div.querySelector(".radio-like").addEventListener("click", () => {
        activeMainHobbySet.add(key);
        renderSubHobbies(key);
        renderMainHobbies();
      });

      // 메인 취미 제거
      const removeBtn = div.querySelector(".remove-btn");
      if (removeBtn) {
        removeBtn.addEventListener("click", e => {
          e.stopPropagation();
          activeMainHobbySet.delete(key);
          removeSubHobbiesByMain(key);
          renderMainHobbies();
        });
      }

      mainCategoryArea.appendChild(div);
    });
  }

  
  const categoryArea = document.querySelector("#CategoryArea");
  const subTitle = document.querySelector("#subTitle");

  function renderSubHobbies(mainKey) {
    subTitle.style.display = "block";

    subHobbyData[mainKey].forEach(hobby => {
      // 이미 있으면 다시 생성 X
      if (document.querySelector(`[data-hobby-id="${hobby.id}"]`)) return;

      const label = document.createElement("label");
      label.dataset.hobbyId = hobby.id;

      const checked = selectedHobbyList.includes(hobby.id) ? "checked" : "";

      label.innerHTML = `
        <input type="checkbox" name="hobbyCode" value="${hobby.id}" ${checked}>
        ${hobby.name}
      `;

      categoryArea.appendChild(label);
    });
  }

  function removeSubHobbiesByMain(mainKey) {
    subHobbyData[mainKey].forEach(hobby => {
      const el = document.querySelector(`[data-hobby-id="${hobby.id}"]`);
      if (el) el.remove();
    });

    if (categoryArea.children.length === 0) {
      subTitle.style.display = "none";
    }
  }

  function initSelectedHobbies() {
    Object.entries(subHobbyData).forEach(([key, list]) => {
      const hasSelected = list.some(h =>
        selectedHobbyList.includes(h.id)
      );

      if (hasSelected) {
        activeMainHobbySet.add(key);
        renderSubHobbies(key);
      }
    });

    renderMainHobbies();
  }

initSelectedHobbies();




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