

const checkObj = {
    "authEmail"       : false,
    "authKey"         : false,
    "memberName"      : false,
    "memberTel"       : false
};

// 이메일 -------------------------------------------------------------
const authEmail = document.querySelector("#memberEmail"); // input이메일
const emailMessage = document.querySelector("#emailMessage"); // 이메일 메시지


// 타이머 초기값(초기화)
let authTimer; // 타이머 역할을 할 setInterval을 저장할 변수

const initMin = 4; // 타이머 초기값 (분)
const initSec = 59; // 타이머 초기값 (초)
const initTime = "05:00";

// 실제 줄어드는 시간을 저장할 변수
let min = initMin;
let sec = initSec;

authEmail.addEventListener("input",e=>{

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
        .then(resp => resp.text()) // text로 받은 이유 단순 이메일 문자라 String으로 받음
        .then(count =>{
            if(count == 0){
                emailMessage.innerText = "가입 정보와 일치하는 이메일이 없습니다.";
                emailMessage.classList.add('error');
                emailMessage.classList.remove('confirm');
                checkObj.authEmail = false;
            }
            else{
                emailMessage.innerText = "가입 정보와 일치하는 이메일입니다.";
                emailMessage.classList.add('confirm');
                emailMessage.classList.remove('error');
                checkObj.authEmail = true; 
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

// 이메일 인증번호 보내기 -------------------------------------------------------------
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn"); // 인증번호 보내기 버튼

sendAuthKeyBtn.addEventListener("click",()=>{
    

    // checkOBJ에가 false 일때 == 중복이거나 유효한 이메일이 아닌경우
    if(!checkObj.authEmail) {
        alert("유효한 이메일 작성 후 클릭해 주세요");
        return;
    }   

        
    // 기존에 돌아가던 타이머 멈추기 (중복 방지)
    clearInterval(authTimer);

    // 시간 변수 초기화(4:59)
    min = initMin;
    sec = initSec;

    // 화면에 즉시 "05:00" 표시만
    authKeyMessage.innerText = initTime;
    authKeyMessage.classList.remove("confirm", "error");

    // 비동기로 서버에서 메일보내기 
    fetch("/email/idSearch",{
        method : "POST",
        headers : {"Content-Type": "application/json"},
        body : JSON.stringify({ "authEmail" : authEmail.value })
    })
    .then(resp => resp.text())
    .then(result => {
        if(result == 1){
            console.log("인증 번호 발송 성공");
            alert("인증번호가 발송되었습니다.");

            min = initMin;
            sec = initSec;
            authKeyMessage.innerText = initTime;
        
            // 1초마다 동작함
            authTimer = setInterval( () => {
        
                authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;
        
                // 0분 0초인 경우 ("00:00" 출력 후)
                if(min == 0 && sec == 0) {
                    checkObj.authKey = false; // 인증 못함
                    clearInterval(authTimer); // interval 멈춤
                    authKeyMessage.classList.add('error');
                    authKeyMessage.classList.remove('confirm');
                    alert("인증시간이 초과되었습니다.");
                    return;
                }
        
                // 0초인 경우(0초를 출력한 후)
                if(sec == 0) {
                    sec = 60;
                    min--;
                }
        
                sec--; // 1초 감소
        
            } , 1000); // 1초 지연시간     
        }else{
            console.log("인증 번호 발송 실패");
            alert("인증번호가 발송 실패되었습니다.");
        }
    })

});

// 전달 받은 숫자가 10 미만인 경우(한자리) 앞에 0 붙여서 반환
function addZero(number) {
    if( number < 10 ) return "0" + number;
    else              return number;
}


// 인증코드 확인 ---------------------------------------------------------------------------
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn"); // 인증하기 버튼
const authKey = document.querySelector("#authKey"); // 인증번호
const authKeyMessage = document.querySelector("#authKeyMessage");

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
            clearInterval(authTimer); // 인증번호 일치한 경우 멈춤
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

// 이름 -------------------------------------------------------

const memberName = document.querySelector("#memberName");
const nameMessage = document.querySelector("#nameMessage");

memberName.addEventListener("input",e=>{

    const inputName = e.target.value; // 입력한 값

    fetch("/member/checkName",{
        method : "POST",
        headers : {"Content-Type": "application/json"},
        body : JSON.stringify({ "memberName" : memberName.value ,"memberEmail" : authEmail.value})
    })
    .then(resp => resp.text())
    .then(count => {
        if(count == 1){
            nameMessage.innerText="가입 정보와 일치하는 이름입니다.";
            nameMessage.classList.add("confirm");
            nameMessage.classList.remove("error");
            checkObj.memberName = true;
        }else{
            console.log("인증번호 불일치");
            nameMessage.innerText="가입 정보와 일치하지 않는 이름입니다.";
            nameMessage.classList.add("error");
            nameMessage.classList.remove("confirm"); 
            checkObj.memberName = false;
        }

        
    })
    
});

//전화번호 ---------------------------------------------------------------------------------------------

const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");

memberTel.addEventListener("input", e => {
    const inputTel = e.target.value;

    // 1) 입력값이 없을 때
    if (inputTel.trim().length === 0) {
        telMessage.innerText = "전화번호를 입력해주세요.";
        telMessage.classList.remove("confirm", "error");
        checkObj.memberTel = false;
        return;
    }

    // 2) 전화번호 정규식 검사 (하이픈 제외 숫자만 11자리 예시)
    const regExp = /^01[016789]\d{7,8}$/;

    if (regExp.test(inputTel)) {
        fetch("/member/checkTel", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ "memberTel": inputTel ,"memberEmail" : authEmail.value})
        })
        .then(resp => resp.text())
        .then(count => {
            if (count == 1) {
                telMessage.innerText = "가입 정보와 일치하는 전화번호입니다.";
                telMessage.classList.add("confirm");
                telMessage.classList.remove("error");
                checkObj.memberTel = true;
            } else {
                telMessage.innerText = "가입 정보와 일치하지 않는 전화번호입니다.";
                telMessage.classList.add("error");
                telMessage.classList.remove("confirm");
                checkObj.memberTel = false;
            }
        });
    } else {
        telMessage.innerText = "유효한 전화번호 형식이 아닙니다.";
        telMessage.classList.add("error");
        telMessage.classList.remove("confirm");
        checkObj.memberTel = false;
    }
});

// 아이디 찾기 버튼 --------------------------------------------------------------------------
const findIdForm = document.querySelector("#idSearchForm"); 

findIdForm.addEventListener("submit", e => {
    // checkObj는 이메일, 인증번호, 이름, 전화번호가 모두 true여야 함
    for(let key in checkObj) {
        if(!checkObj[key]) {
            let str;
            switch(key) {
                case "authEmail": str = "이메일이 유효하지 않습니다."; break;
                case "authKey": str = "인증번호를 확인해주세요."; break;
                case "memberName": str = "이름을 확인해주세요."; break;
                case "memberTel": str = "전화번호를 확인해주세요."; break;
            }
            alert(str);
            e.preventDefault(); // 제출 방지
            return;
        }
    }
});