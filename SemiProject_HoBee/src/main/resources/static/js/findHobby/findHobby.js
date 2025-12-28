<<<<<<< HEAD
const name = document.querySelector(".name-input").value;

document.querySelector(".start-btn").addEventListener("click", () => {

  location.href = "/findHobby/start";

});

=======
const inputName = document.querySelector(".name-input");

document.querySelector(".start-btn").addEventListener("click", () => {

  location.href = "/findHobby/start?inputName=" + inputName.value;

});
>>>>>>> cac63ee4428cb72110908c7fb2d02804905df2be
