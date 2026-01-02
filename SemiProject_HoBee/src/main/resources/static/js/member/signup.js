// 회원 가입 페이지


// 유효성 검사
// 유효하면 true
// 유효하지 않으면 false
const checkObj = {
    "authEmail"     : false,
    "memberId"        : false,
    "authKey"         : false,
    "memberPw"        : false,
    "memberPwConfirm" : false,
    "memberNickname"  : false,
    "memberTel"       : false
};


// 이메일 ---------------------------------------------------------------------------------------------------------------------
const authEmail = document.querySelector("#memberEmail");     // 이메일
const emailMessage = document.querySelector("#emailMessage");   // 이메일 span 메세지 

    // 1) 입력된 이메일이 없을 경우
authEmail.addEventListener("input", e => {

    const inputEmail = e.target.value; // 입력된 값 가져오기

    // 1) 입력된 이메일이 없을 경우
    if(inputEmail.trim().length === 0) {
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";
        emailMessage.classList.remove('confirm', 'error');
        checkObj.authEmail = false;
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
                checkObj.authEmail = true; // 형식 o / 중복 x
            }
            else{
                emailMessage.innerText = "이미 사용 중인 이메일입니다.";
                emailMessage.classList.add('error');
                emailMessage.classList.remove('confirm');
                checkObj.authEmail = false;
                checkObj.authKey = false;
            }
        })
        
    }else{
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add('error');
        emailMessage.classList.remove('confirm');
        checkObj.authEmail = false;
        checkObj.authKey = false;
    }
   
    
});

// 인증 코드 보내기 ------------------------------------------------------------------------------------
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn"); // 인증번호 받기 버튼
const authKeyMessage = document.querySelector("#authKeyMessage"); // 인증 span 메세지

sendAuthKeyBtn.addEventListener("click",()=>{
    

    // checkOBJ에가 false 일때 == 중복이거나 유효한 이메일이 아닌경우
    if(!checkObj.authEmail) {
        alert("유효한 이메일 작성 후 클릭해 주세요");
        return;
    }   

    // 비동기로 서버에서 메일보내기 
    fetch("/email/signup",{
        method : "POST",
        headers : {"Content-Type": "application/json"},
        body : JSON.stringify({ "authEmail" : authEmail.value })
    })
    .then(resp => resp.text())
    .then(result => {
        if(result == 1){
            console.log("인증 번호 발송 성공");
            alert("인증번호가 발송되었습니다.");
        }else{
            console.log("인증 번호 발송 실패");
            alert("인증번호가 발송 실패되었습니다.");
        }
    })

});


// 인증코드 확인
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn"); // 인증하기 버튼
const authKey = document.querySelector("#authKey"); // 인증번호

checkAuthKeyBtn.addEventListener("click" , e=>{

    const inputAuthKey = authKey.value; // 입력한 값 얻어오기

    if(inputAuthKey.trim().length === 0){
        alert("인증번호 작성 후 클릭해주세요.");
        authKeyMessage.classList.remove('confirm', 'error');
        checkObj.authKey = false;
        return;
    }

    fetch("/email/checkAuthKey",{
        method : "POST",
        headers : {"Content-Type": "application/json"},
        body : JSON.stringify({ "authKey" : authKey.value ,"authEmail":authEmail.value})
    })
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){
            console.log("인증번호 일치");
            authKeyMessage.innerText="인증번호가 일치합니다";
            authKeyMessage.classList.add("confirm");
            authKeyMessage.classList.remove("error");
            checkObj.authKey = true;
        }else{
            console.log("인증번호 불일치");
            authKeyMessage.innerText="인증번호가 일치하지 않습니다";
            authKeyMessage.classList.add("error");
            authKeyMessage.classList.remove("confirm"); 
            checkObj.authKey = false;
        }

        
    })

});

// 아이디 중복 검사 ------------------------------------------------------------------------------------

const memberId = document.querySelector("#memberId");
const idMessage = document.querySelector("#idMessage");

memberId.addEventListener("input",e=>{

    const inputId = e.target.value; // 입력된 값 가져오기

    // 1) 입력된 아이디가 없을 경우
    if(inputId.trim().length === 0) {
        idMessage.innerText = "아이디를 입력해주세요.";
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

// 비밀번호-------------------------------------------------------------------------------------------------

const memberPw = document.querySelector("memberPw");
const pwMessage = document.querySelector("pwMessage");

/^(?=.*[!@#$%^&*()_+|~=`{}\[\]:";'<>?,.\/]).{6,12}$/

memberPw.addEventListener("input" , e=>{

    const inputPw = e.target.value;

    if(inputPw.trim().length === 0){
        pwMessage.innerText = "비밀번호를 입력해주세요.";
        pwMessage.classList.remove('confirm', 'error');
        checkObj.memberPw = false;
        return;
    }

});






// 닉네임 중복 검사 ----------------------------------------------------------------------------------------------

const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

memberNickname.addEventListener("input" ,e=>{

    const inputNickname = e.target.value;

    if(inputNickname.trim().length === 0){
        nickMessage.innerText="닉네임을 입력해주세요"
        nickMessage.classList.remove('confirm', 'error');
        checkObj.memberNickname = false;
        return;
    }

    const regExp = /^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]{2,8}$/;


    if(regExp.test(inputNickname)){

        fetch("/member/checkNickname?memberNickname="+inputNickname) 
        .then(resp => resp.text()) // text로 받은 이유 단순 이메일 문자라 String으로 받음
        .then(count =>{
     
                if(count == 0){
                    nickMessage.innerText = "사용 가능한 닉네임입니다.";
                    nickMessage.classList.add('confirm');
                    nickMessage.classList.remove('error');
                    checkObj.memberNickname = true; // 형식 o / 중복 x
                }
                else{
                    nickMessage.innerText = "이미 사용 중인 닉네임입니다.";
                    nickMessage.classList.add('error');
                    nickMessage.classList.remove('confirm');
                    checkObj.memberNickname = false;
                }
            

        })
    }else{
        nickMessage.innerText = "2~8 사이로 입력해주세요.";
        nickMessage.classList.add('error');
        nickMessage.classList.remove('confirm');
        checkObj.memberNickname = false;
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