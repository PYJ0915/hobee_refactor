const question = document.querySelector(".question-title");

const answerBtns = document.querySelectorAll(".answer");

answerBtns.forEach((btn) => {

  btn.addEventListener("click", nextQuestion())

});

function nextQuestion() {

  fetch("/findHobby/selectQuestionList")
  .then(resp => resp.json())
  .then(questionList => {

    let i = 1;

    for(let inputQuestion of questionList) {
      question.innerText = "Q" + i + "." + inputQuestion;
      i++
    }

  });

}