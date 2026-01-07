/* ***** 댓글 목록 조회(ajax) ***** */

const selectCommentList = () => {

  fetch("/comment?boardNo=" + boardNo)
    .then(resp => resp.json())
    .then(commentList => {

      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {

        const commentRow = document.createElement("li");
        commentRow.classList.add("comment-item");

        if (comment.commentNo2 != 0) {
          commentRow.classList.add("child-comment");
        }

        /* ================= 상단 영역 (무조건 생성) ================= */
        const commentTop = document.createElement("div");
        commentTop.classList.add("comment-top");

        const commentWriter = document.createElement("div");
        commentWriter.classList.add("comment-writer");

        const profileImg = document.createElement("img");
        profileImg.classList.add("profile-img");
        profileImg.src = comment.profileImg ?? userDefaultIamge;

        const nickname = document.createElement("span");
        nickname.classList.add("nickname");
        nickname.innerText = comment.memberNickname;

        const commentDate = document.createElement("span");
        commentDate.classList.add("comment-date");
        commentDate.innerText = comment.commentWriteDate;

        commentWriter.append(profileImg, nickname, commentDate);
        commentTop.append(commentWriter);

        /* ---------- 우측 상단 버튼 (삭제 안 된 댓글만) ---------- */
        if (comment.commentDelFl !== "Y") {

          const topActions = document.createElement("div");
          topActions.classList.add("comment-actions", "top-actions");

          const replyBtn = document.createElement("button");
          replyBtn.classList.add("reply-btn");
          replyBtn.innerText = "답글";
          replyBtn.onclick = () => showInsertComment(comment.commentNo, replyBtn);

          const reportBtn = document.createElement("button");
          reportBtn.type = "button";
          reportBtn.classList.add("report-btn", "comment-report");
          reportBtn.innerText = "신고";
          reportBtn.dataset.writer = comment.memberNickname;
          reportBtn.dataset.reportedMemberNo = comment.memberNo;
          reportBtn.dataset.targetNo = comment.commentNo;

          topActions.append(replyBtn, reportBtn);
          commentTop.append(topActions);
          commentRow.append(commentTop);
        }



        /* ================= 댓글 내용 ================= */
        const content = document.createElement("p");
        content.classList.add("comment-content");

        if (comment.commentDelFl === "Y") {
          content.classList.add("deleted");
          content.innerText = "삭제된 댓글입니다.";
        } else {
          content.innerText = comment.commentContent;
        }

        commentRow.append(content);


        /* ================= 하단 버튼 (본인 댓글만) ================= */
        if (loginMemberNo && loginMemberNo === comment.memberNo && comment.commentDelFl !== "Y") {

          const bottomActions = document.createElement("div");
          bottomActions.classList.add("comment-actions", "bottom-actions");

          const editBtn = document.createElement("button");
          editBtn.classList.add("edit-btn");
          editBtn.classList.add("fa-solid");
          editBtn.classList.add("fa-pen");
          editBtn.onclick = () => showUpdateComment(comment.commentNo, editBtn);

          const deleteBtn = document.createElement("button");
          deleteBtn.classList.add("del-btn");
          deleteBtn.classList.add("fa-solid");
          deleteBtn.classList.add("fa-trash");
          deleteBtn.onclick = () => deleteComment(comment.commentNo);

          bottomActions.append(editBtn, deleteBtn);
          commentRow.append(bottomActions);
        }

        ul.append(commentRow);
      }
    });
};



//selectCommentList();

// -----------------------------------------------------------------------

/* ***** 댓글 등록(ajax) ***** */

const addContent = document.querySelector("#submit-btn"); // button
const commentContent = document.querySelector("#commentContent"); // textarea

// 댓글 등록 버튼 클릭 시
addContent.addEventListener("click", e => {

  // 로그인이 되어있지 않은 경우
  if (loginMemberNo == null) {
    alert("로그인 후 이용해 주세요");
    return; // early return;
  }

  // 댓글 내용이 작성되지 않은 경우
  if (commentContent.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    commentContent.focus();
    return;
  }


  // ajax를 이용해 댓글 등록 요청
  const data = {
    "commentContent": commentContent.value,
    "boardNo": boardNo,
    "memberNo": loginMemberNo  // 또는 Session 회원 번호 이용도 가능
  };

  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data) // data 객체를 JSON 문자열로 변환
  })

    .then(response => response.text())
    .then(result => {

      if (result > 0) {
        alert("댓글이 등록 되었습니다");
        commentContent.value = ""; // 작성한 댓글 내용 지우기
        selectCommentList(); // 댓글 목록을 다시 조회해서 화면에 출력

      } else {
        alert("댓글 등록 실패");
      }

    })
    .catch(err => console.log(err));
})


/** 답글 작성 화면 추가
 * @param {*} commentNo2 
 * @param {*} btn 
 */
const showInsertComment = (commentNo2, btn) => {

  // 답글 textarea 1개만 허용
  const temp = document.getElementsByClassName("commentInsertContent");

  if (temp.length > 0) {
    if (confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?")) {
      temp[0].nextElementSibling.remove(); // 버튼 영역 삭제
      temp[0].remove(); // textarea 삭제
    } else {
      return;
    }
  }

  // 댓글 li
  const commentItem = btn.closest(".comment-item");

  /* 수정/삭제 영역 숨기기 */
  const bottomActions = commentItem.querySelector(".bottom-actions");
  if (bottomActions) bottomActions.style.display = "none";

  // textarea 생성
  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");

  // 댓글 내용 아래에 삽입
  const commentContent = commentItem.querySelector(".comment-content");
  commentContent.after(textarea);

  // 버튼 영역 생성
  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  const insertBtn = document.createElement("button");
  insertBtn.classList.add("reply-insert");
  insertBtn.innerText = "등록";
  insertBtn.setAttribute(
    "onclick",
    `insertChildComment(${commentNo2}, this)`
  );

  const cancelBtn = document.createElement("button");
  cancelBtn.type = "button";
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  // 버튼 추가
  commentBtnArea.append(insertBtn, cancelBtn);

  // textarea 뒤에 버튼 영역 추가 (⭐ 한 번만)
  textarea.after(commentBtnArea);
};



// ---------------------------------------

/** 답글 (자식 댓글) 작성 취소 
 * @param {*} cancelBtn : 취소 버튼
 */
const insertCancel = (cancelBtn) => {

  const commentItem = cancelBtn.closest(".comment-item");

  // 취소 버튼 부모의 이전 요소(textarea) 삭제
  cancelBtn.parentElement.previousElementSibling.remove();

  // 취소 버튼이 존재하는 버튼영역 삭제
  cancelBtn.parentElement.remove();

  const bottomActions = commentItem.querySelector(".bottom-actions");
  if (bottomActions) bottomActions.style.display = "flex";
}


/** 답글 (자식 댓글) 등록
 * @param {*} commentNo2 : 부모 댓글 번호
 * @param {*} btn  :  클릭된 등록 버튼
 */
const insertChildComment = (commentNo2, btn) => {

  // 답글 내용이 작성된 textarea
  const textarea = btn.parentElement.previousElementSibling;

  // 유효성 검사
  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  // ajax를 이용해 댓글 등록 요청
  const data = {
    "commentContent": textarea.value,
    "boardNo": boardNo,
    "memberNo": loginMemberNo,  // 또는 Session 회원 번호 이용도 가능
    "commentNo2": commentNo2 // 부모 댓글 번호
  };

  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data) // data 객체를 JSON 문자열로 변환
  })

    .then(response => response.text())
    .then(result => {

      if (result > 0) {
        alert("답글이 등록 되었습니다");
        selectCommentList(); // 댓글 목록을 다시 조회해서 화면에 출력

      } else {
        alert("답글 등록 실패");
      }

    })
    .catch(err => console.log(err));



}


// --------------------------------------------------

/** 댓글 삭제
 * @param {*} commentNo 
 */
const deleteComment = commentNo => {

  // 취소 선택 시
  if (!confirm("삭제 하시겠습니까?")) return;

  fetch("/comment", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: commentNo
  })
    .then(resp => resp.text())
    .then(result => {

      if (result > 0) {
        alert("삭제 되었습니다");
        selectCommentList(); // 다시 조회해서 화면 다시 만들기

      } else {
        alert("삭제 실패");
      }

    })
    .catch(err => console.log(err));

}


// ----------------------------------


/** 댓글 수정 화면 전환
 * @param {*} commentNo 
 * @param {*} btn 
 */
const showUpdateComment = (commentNo, btn) => {

  // 이미 열려 있는 수정 textarea
  const temp = document.querySelector(".update-textarea");

  if (temp != null) {

    if (!confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정 하시겠습니까?")) {
      return;
    }

    // 기존 수정 상태 강제 종료
    const prevCommentRow = temp.closest(".comment-item");

    // textarea 제거
    temp.remove();

    // 버튼 영역 제거
    prevCommentRow.querySelector(".comment-btn-area")?.remove();

    // editing 클래스 제거
    prevCommentRow.classList.remove("editing");
  }

  // -------------------------------
  // 여기서부터 새 수정창 열기
  // -------------------------------

  const commentRow = btn.closest("li");
  commentRow.classList.add("editing");

  const contentEl = commentRow.querySelector(".comment-content");
  const beforeContent = contentEl.innerText;

  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = beforeContent;

  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  const updateBtn = document.createElement("button");
  updateBtn.classList.add("update-insert")
  updateBtn.innerText = "수정";
  updateBtn.onclick = () => updateComment(commentNo, updateBtn);

  const cancelBtn = document.createElement("button");
  cancelBtn.type = "button";
  cancelBtn.innerText = "취소";
  cancelBtn.onclick = () => updateCancel(cancelBtn);

  commentBtnArea.append(updateBtn, cancelBtn);

  contentEl.after(textarea, commentBtnArea);

  // UX 보너스 ✨
  textarea.focus();
};



// --------------------------------------------------------------------

/** 댓글 수정 취소
 * @param {*} btn : 취소 버튼
 */
const updateCancel = (btn) => {

  if (confirm("취소 하시겠습니까?")) {
    const commentRow = btn.closest("li"); // 기존 댓글 행
    // textarea 제거
    commentRow.querySelector(".update-textarea").remove();

    // 버튼 영역 제거
    btn.parentElement.remove();

    // ⭐ 기존 댓글 다시 표시
    commentRow.classList.remove("editing");
  }

}


// ----------------------------------------------------------

/** 댓글 수정
 * @param {*} commentNo : 수정할 댓글 번호
 * @param {*} btn       : 클릭된 수정 버튼
 */
const updateComment = (commentNo, btn) => {

  // 수정된 내용이 작성된 textarea 얻어오기
  const textarea = btn.parentElement.previousElementSibling;

  // 유효성 검사
  if (textarea.value.trim().length == 0) {
    alert("댓글 작성 후 수정 버튼을 클릭해 주세요");
    textarea.focus();
    return;
  }

  // 댓글 수정 (ajax)
  const data = {
    "commentNo": commentNo,
    "commentContent": textarea.value
  }

  fetch("/comment", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
    .then(resp => resp.text())
    .then(result => {
      if (result > 0) {
        alert("댓글이 수정 되었습니다");
        selectCommentList();
      } else {
        alert("댓글 수정 실패");
      }

    })
    .catch(err => console.log(err));
}
