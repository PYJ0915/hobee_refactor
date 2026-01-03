$(document).ready(() => {


    $('#summernote').summernote({
        height: 400,
        lang: 'ko-KR',
        // 툴바 설정 추가
        toolbar: [
            // [그룹명, [버튼 리스트]]
            ['style', ['style']], // 글머리 양식
            ['font', ['bold', 'underline', 'clear']], // 글자 굵게, 밑줄, 서식 지우기
            ['fontname', ['fontname']], // 글꼴 설정
            ['color', ['color']], // 글자 색상
            ['para', ['ul', 'ol', 'paragraph']], // 리스트, 문단 정렬
            // ['table', ['table']], // 표 만들기 -> 구현은 되지만 표가 제대로 출력이 되지않는 문제 있어 삭제
            ['insert', ['picture']], // 링크, 이미지, 비디오 -> 링크와 비디오는 구현이 쉽지 않아 삭제
            ['view', ['codeview', 'help']] // 전체화면, 소스코드 보기, 도움말 -> 전체화면 구현이 미흡하고 조잡해 삭제
        ],
        callbacks: {
            onImageUpload: (files) => { // 이미지 업로드 콜백
                uploadImage(files[0], $('#summernote'));
            }
        }
    });
});

const uploadImage = (file, editor) => {
    let data = new FormData();
    data.append("file", file);
    
    $.ajax({
        url: "/editBoard/imageUpload",
        type: "POST",
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: (url) => { // 성공 시 콜백
            $(editor).summernote('insertImage', url);
        },
        error: (e) => { // 실패 시 콜백
            console.log(e);
        }
    });
};

const form = document.querySelector("#boardContent");

if (form != null) {
    form.addEventListener("submit", e => {

        // 1. 제목 검사
        const boardTitle = document.querySelector("[name=boardTitle]");
        if (boardTitle.value.trim().length === 0) {
            alert("제목을 작성해주세요");
            boardTitle.focus();
            e.preventDefault();
            return;
        }

        // 2. 내용 검사 (써머노트 방식)
        const boardContent = $('#summernote').summernote('code');

        // 태그를 모두 제거하고 순수 텍스트만 남겨서 비어있는지 확인
        const contentText = boardContent.replace(/<[^>]*>?/gm, '').trim();

        if (contentText.length === 0) {
            alert("내용을 작성해주세요");
            $('#summernote').summernote('focus');
            e.preventDefault();
            return;
        }

         $('#summernote').val(content);

    });
}
