// 로그인 

const loginId = document.querySelector("#memberId");
const loginPw = document.querySelector("#memberPw");
const loginbtn = document.querySelector("#login-btn");
const login = document.querySelector("#login");
const loginMember = document.querySelector("#loginMmeber");
const saveId = document.querySelector("#saveId");

// 체크박스 체크
const saveCookieId = getCookie("saveId"); // 이메일 또는 undefined 쿠기객체

function getCookie(Cookiekey) { // 이렇게 하는 이유는 모든 쿠키가 한 줄로 나옴 (필요한 부분만 잘라서 사용하기 위해)
    const cookies = document.cookie.split('; ');
    for (let c of cookies) {
        let [k, v] = c.split('=');
        if (k === Cookiekey) return v;
    }
    return undefined;
}

if(saveId != null){ // 아이디 저장 체크

    if(saveCookieId != undefined){
    
        // 쿠키 아이디로 넣기              
        loginId.value = saveCookieId;
    
        // 아이디 저장 체크박스에 체크해두기
        saveId.checked = true;
    }
}



//로그인 전
loginbtn.addEventListener("click",(e)=>{

     // 아이디 창에 아무것도 적지 않았을 때
    if(loginId.value.trim().length === 0){ 
        
        alert("아이디 입력해주세요");
        e.preventDefault(); // 제출을 막아줌!!
        return;
            
    }

    // 비밀번호 창에 아무것도 적지 않았을 때
    if(loginPw.value.trim().length === 0){ 
        
        alert("비밀번호 입력해주세요");
        e.preventDefault();
        return;
            
    }

    console.log(saveId);    


 });










