const inputName = document.querySelector(".name-input");

document.querySelector(".start-btn").addEventListener("click", () => {

  location.href = "/findHobby/start?inputName=" + inputName.value;

});

