// 로그인 

const loginId = document.querySelector("#memberId")
const loginPw = document.querySelector("#memberPw")
const loginbtn = document.querySelector("#login-btn")
const login = document.querySelector("#login");

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


 });





