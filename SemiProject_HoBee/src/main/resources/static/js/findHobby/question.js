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

    let i = 1; // 질문 순서

    // 질문 내용을 사용자에게 제공 (배열 순회)
    for (let inputQuestion of questionList) {
      question.innerText = "Q" + i + ". " + inputQuestion.questionContent;

      let questionNo = inputQuestion.questionNo;

      answerBtns.forEach(answerBtn => {

        // 버튼 클릭 시 다음 질문 제공 + 점수 누적
        answerBtn.addEventListener("click", e => {

          if (e.target.innerText == "모르겠다") return;

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
            });
        });
      });

      if(i == questionList.length()) {

        const scoreMap = new Map();
        scoreMap.set(sports, "sports");
        scoreMap.set(art, "art");
        scoreMap.set(selfDevelop, "selfDevelop");
        scoreMap.set(social, "social");
        scoreMap.set(shopping, "shopping");

        const scoreArr = [sports, art, selfDevelop, social, shopping];
        scoreArr.sort((a,b) => b - a);

        const firstHobby = scoreMap.get(scoreArr[0]);
        const secondHobby = scoreMap.get(scoreArr[1]);

        location.href = "/findHobby/end?firstHobby=" + firstHobby + "&secondHobby=" + secondHobby;

      }

    }
  });