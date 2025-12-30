// 회원 가입 페이지


// 유효성 검사
// 유효하면 true
// 유효하지 않으면 false
const checkObj = {
    "memberEmail"     : false,
    "memberId"        : false,
    "authKey"         : false,
    "memberPw"        : false,
    "memberPwConfirm" : false,
    "memberNickname"  : false,
    "memberTel"       : false
};


// 이메일 ---------------------------------------------------------------------------------------------------------------------
const memberEmail = document.querySelector("#memberEmail");     // 이메일
const emailMessage = document.querySelector("#emailMessage");   // 이메일 span 메세지 

    // 1) 입력된 이메일이 없을 경우
memberEmail.addEventListener("input", e => {

    const inputEmail = e.target.value; // 입력된 값 가져오기

    // 1) 입력된 이메일이 없을 경우
    if(inputEmail.trim().length === 0) {
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";
        emailMessage.classList.remove('confirm', 'error');
        checkObj.memberEmail = false;
        return;
    }

    // 2) 이메일 정규표현식 유효성 ,중복 검사
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if(regExp.test(inputEmail)) { // 유효한 경우
        
        fetch("/member/checkEmail?memberEmail="+inputEmail) 
        // ex)http://localhost/member/checkEmail?memberEmail=abc@test.com 단순 DB조회라 GET방식 커리스트링 이용 ,즉 파람으로 갖고 오기 위해
        .then(resp => resp.text()) // text로 받은 이유 단순 이메일 문자라 String으로 받음
        .then(count =>{
            if(count == 0){
                emailMessage.innerText = "사용 가능한 이메일입니다.";
                emailMessage.classList.add('confirm');
                emailMessage.classList.remove('error');
                checkObj.memberEmail = true; // 형식 o / 중복 x
            }
            else{
                emailMessage.innerText = "이미 사용 중인 이메일입니다.";
                emailMessage.classList.add('error');
                emailMessage.classList.remove('confirm');
                checkObj.memberEmail = false;
            }
        })
        
    }else{
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add('error');
        emailMessage.classList.remove('confirm');
        checkObj.memberEmail = false;
    }
   
    
});

// 인증 코드 보내기 ------------------------------------------------------------------------------------
// const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn"); // 인증 버튼
// const authKeyMessage = document.querySelector("#authKeyMessage"); // 인증 span 메세지

// sendAuthKeyBtn.addEventListener("click",()=>{});


// 아이디 중복 검사 ------------------------------------------------------------------------------------

const memberId = document.querySelector("#memberId");
const idMessage = document.querySelector("#idMessage");

memberId.addEventListener("input",e=>{

    const inputId = e.target.value; // 입력된 값 가져오기

    // 1) 입력된 아이디가 없을 경우
    if(inputId.trim().length === 0) {
        idMessage.innerText = "영어/숫자 6~12 사이로 입력해주세요.";
        idMessage.classList.remove('confirm', 'error');
        checkObj.memberId = false;
        return;
    }

    // 6~12 글자 제한(왜 하냐 html에서하면 우회하는 사람이 있을 수 있기에)
    const regExp = /^[a-zA-Z0-9]{6,12}$/;

    if(regExp.test(inputId)){

        fetch("/member/checkId?memberId="+inputId) 
        .then(resp => resp.text()) // text로 받은 이유 단순 이메일 문자라 String으로 받음
        .then(count =>{
     
                if(count == 0){
                    idMessage.innerText = "사용 가능한 아이디입니다.";
                    idMessage.classList.add('confirm');
                    idMessage.classList.remove('error');
                    checkObj.memberId = true; // 형식 o / 중복 x
                }
                else{
                    idMessage.innerText = "이미 사용 중인 아이디입니다.";
                    idMessage.classList.add('error');
                    idMessage.classList.remove('confirm');
                    checkObj.memberId = false;
                }
            
        })
    }else{
        idMessage.innerText = "영어/숫자 6~12 사이로 입력해주세요.";
        idMessage.classList.add('error');
        idMessage.classList.remove('confirm');
        checkObj.memberId = false;
    }


});







































































// 주소 검색 버튼 클릭 시
document.querySelector("#searchAddress").addEventListener("click",execDaumPostcode);    

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
// 주소 검색 버튼 클릭 시
document.querySelector("#searchAddress").addEventListener("click",execDaumPostcode);