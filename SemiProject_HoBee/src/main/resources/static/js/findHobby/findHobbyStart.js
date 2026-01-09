const inputName = document.querySelector(".name-input");

document.querySelector(".start-btn").addEventListener("click", () => {
  localStorage.setItem("inputName", inputName.value);
  location.href = "/findHobby/start";
});

