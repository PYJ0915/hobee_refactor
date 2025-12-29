// 질문이 들어갈 곳 요소를 얻어옴
const question = document.querySelector(".question-title");

// 사용자 누를 대답 버튼 요소를 한 번에 얻어옴
const answerBtns = document.querySelectorAll(".answer");

// 비동기로 질문 리스트를 얻어오는 요청을 보냄
fetch("/findHobby/selectQuestionList")
  .then(resp => resp.json())
  .then(questionList => {

    let sports = 0;
    let art = 0;
    let selfDevelop = 0;
    let social = 0;
    let shopping = 0;

    let index = 0;

    question.innerText = "Q" + 1 + ". " + questionList[0].questionContent;

    answerBtns.forEach(answerBtn => {

      // 버튼 클릭 시 다음 질문 제공 + 점수 누적
      answerBtn.addEventListener("click", e => {

        let questionNo = questionList[index].questionNo;

        if (e.target.innerText == "모르겠다") {
          if (index == questionList.length - 1) {
            lastQuestion();
            return;
          } else {
            nextQuestion();
            return;
          }
        }

        fetch("/findHobby/selectScore?questionNo=" + questionNo)
          .then(resp => resp.json())
          .then(questionScoreList => {

            if (e.target.innerText == "그렇다") { // 그렇다 클릭 시 점수 가산
              for (let questionScore of questionScoreList) {
                switch (questionScore.hobbyCode) {
                  case 1:
                    sports += questionScore.answerScore;
                    break;
                  case 2:
                    art += questionScore.answerScore;
                    break;
                  case 3:
                    selfDevelop += questionScore.answerScore;
                    break;
                  case 4:
                    social += questionScore.answerScore;
                    break;
                  case 5:
                    shopping += questionScore.answerScore;
                    break;
                }
              }
            } else { // 그렇지 않다 클릭 시 점수 차감
              for (let questionScore of questionScoreList) {
                switch (questionScore.hobbyCode) {
                  case 1:
                    sports -= questionScore.answerScore;
                    break;
                  case 2:
                    art -= questionScore.answerScore;
                    break;
                  case 3:
                    selfDevelop -= questionScore.answerScore;
                    break;
                  case 4:
                    social -= questionScore.answerScore;
                    break;
                  case 5:
                    shopping -= questionScore.answerScore;
                    break;
                }
              }
            }

            if (index == questionList.length - 1) {
              lastQuestion();
            } else {
              nextQuestion();
            }
          });
      });
    });

    function nextQuestion() {
      index++;
      question.innerText = "Q" + (index + 1) + ". " + questionList[index].questionContent;
    }

    function lastQuestion() {
      const scores = [
        { hobby: "sports", score: sports },
        { hobby: "art", score: art },
        { hobby: "selfDevelop", score: selfDevelop },
        { hobby: "social", score: social },
        { hobby: "shopping", score: shopping }
      ];

      scores.sort((a, b) => b.score - a.score);

      const firstHobby = scores[0].hobby;
      const secondHobby = scores[1].hobby;

      location.href = "/findHobby/end?firstHobby=" + firstHobby + "&secondHobby="   + secondHobby;
    }

  });