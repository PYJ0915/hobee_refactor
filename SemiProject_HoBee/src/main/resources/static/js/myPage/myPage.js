/** 회원 정보 수정 페이지 **/

const updateInfo = document.querySelector("#updateInfo"); // form 태그

// #updateInfo 요소가 존재 할 때만 수행
if (updateInfo != null) {

	// 주소 검색 버튼 클릭 시
	document.querySelector(".address-btn").addEventListener("click", execDaumPostcode);

	// 다음 주소 API
	function execDaumPostcode() {
		new daum.Postcode({
			oncomplete: function(data) {
				// 팝업에서 검색결과 항목을 클릭했을 때 실행할 코드를 작성하는 부분.

				// 각 주소의 노출 규칙에 따라 주소를 조합한다.
				// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
				var addr = ''; // 주소 변수

				// 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
				if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
					addr = data.roadAddress;
				} else { // 사용자가 지번 주소를 선택했을 경우(J)
					addr = data.jibunAddress;
				}

				// 우편번호와 주소 정보를 해당 필드에 넣는다.
				document.getElementById('postcode').value = data.zonecode;
				document.getElementById("address").value = addr;
				// 커서를 상세주소 필드로 이동한다.
				document.getElementById("detailAddress").focus();
			}
		}).open();
	}


	/*********************************************************
	 * 초기 선택된 취미 (서버 → JS)
	 *********************************************************/
	let selectedHobbyList = window.selectedHobbyList || [];

	const hiddenHobbyArea = document.querySelector("#hiddenHobbyArea");

	/*********************************************************
	 * 취미 데이터
	 *********************************************************/
	const subHobbyData = {
		sports: [
			{ id: 101, name: "러닝" }, { id: 102, name: "헬스" }, { id: 103, name: "등산" },
			{ id: 104, name: "사이클" }, { id: 105, name: "볼링" }, { id: 106, name: "탁구" },
			{ id: 107, name: "수영" }, { id: 108, name: "축구" }, { id: 109, name: "야구" }, { id: 110, name: "골프" }
		],
		art: [
			{ id: 201, name: "드로잉" }, { id: 202, name: "캘리그라피" }, { id: 203, name: "사진 촬영" },
			{ id: 204, name: "영상 편집" }, { id: 205, name: "악기 연주" }, { id: 206, name: "도예/공예" },
			{ id: 207, name: "전시회·공연 관람" }
		],
		selfDevelop: [
			{ id: 301, name: "독서" }, { id: 302, name: "글쓰기" }, { id: 303, name: "코딩" },
			{ id: 304, name: "외국어 공부" }, { id: 305, name: "요리" }, { id: 306, name: "재테크/투자" }
		],
		social: [
			{ id: 401, name: "봉사활동" }, { id: 402, name: "북클럽" }, { id: 403, name: "보드게임 모임" },
			{ id: 404, name: "취미 클래스" }, { id: 405, name: "스포츠 동호회" },
			{ id: 406, name: "스터디 모임" }, { id: 407, name: "그룹 운동" }
		],
		shopping: [
			{ id: 501, name: "피규어/굿즈 수집" }, { id: 502, name: "음반/LP 수집" },
			{ id: 503, name: "향수 수집" }, { id: 504, name: "패션 아이템 수집" },
			{ id: 505, name: "문구류 수집" }, { id: 506, name: "한정판/콜라보 수집" }
		]
	};

	/*********************************************************
	 * 메인 카테고리 관련
	 *********************************************************/
	let activeMainHobby = null;

	const mainCategoryArea = document.querySelector("#mainCategoryArea");
	const categoryArea = document.querySelector("#CategoryArea");
	const subTitle = document.querySelector("#subTitle");

	function getMainHobbyName(key) {
		return {
			art: "문화·예술",
			selfDevelop: "자기계발",
			sports: "운동·레저",
			social: "사회 교류",
			shopping: "수집·소비"
		}[key];
	}

	/*********************************************************
	 * 유틸
	 *********************************************************/
	function findHobbyIdByName(name) {
		for (const list of Object.values(subHobbyData)) {
			const found = list.find(h => h.name === name);
			if (found) return found.id;
		}
		return null;
	}

	/*********************************************************
	 * 기존 서버 렌더링 취미 태그에 X 버튼 주입
	 *********************************************************/
	function enhanceExistingHobbyTags() {
		document.querySelectorAll(".hobby-tag").forEach(tag => {
			if (tag.querySelector(".remove-btn")) return;

			const hobbyName = tag.textContent.trim();
			const hobbyId = findHobbyIdByName(hobbyName);
			if (!hobbyId) return;

			tag.dataset.id = hobbyId;

			const btn = document.createElement("button");
			btn.type = "button";
			btn.className = "remove-btn";
			btn.textContent = "×";

			btn.addEventListener("click", () => removeHobbyById(hobbyId, tag));

			tag.appendChild(btn);
		});
	}

	/*********************************************************
	 * 취미 제거 로직
	 *********************************************************/
	function removeHobbyById(id, tagElement) {
		// 상태 제거
		selectedHobbyList = selectedHobbyList.filter(v => v !== id);

		// hidden input 제거
		[...hiddenHobbyArea.children].forEach(input => {
			if (Number(input.value) === id) input.remove();
		});

		// 체크박스 해제
		const checkbox = document.querySelector(
			`#CategoryArea input[value="${id}"]`
		);
		if (checkbox) checkbox.checked = false;

		// 화면 제거
		tagElement.remove();
	}

	/*********************************************************
	 * hidden input 동기화 (추가 전용)
	 *********************************************************/
	function syncSelectedSubHobbies() {
		const checked = document.querySelectorAll('#CategoryArea input:checked');

		checked.forEach(cb => {
			const id = Number(cb.value);
			if (!selectedHobbyList.includes(id)) {
				selectedHobbyList.push(id);

				const hidden = document.createElement("input");
				hidden.type = "hidden";
				hidden.name = "hobbyCode";
				hidden.value = id;

				hiddenHobbyArea.appendChild(hidden);
			}
		});
	}

	/*********************************************************
	 * 메인 취미 렌더링
	 *********************************************************/
	function renderMainHobbies() {
		mainCategoryArea.innerHTML = "";

		Object.keys(subHobbyData).forEach(key => {
			const label = document.createElement("label");
			label.className =
				"radio-like" + (activeMainHobby === key ? " checked" : "");
			label.textContent = getMainHobbyName(key);

			label.addEventListener("click", () => {
				if (activeMainHobby === key) return;

				syncSelectedSubHobbies();
				categoryArea.innerHTML = "";
				subTitle.style.display = "none";

				activeMainHobby = key;
				renderSubHobbies(key);
				renderMainHobbies();
			});

			mainCategoryArea.appendChild(label);
		});
	}

	/*********************************************************
	 * 서브 취미 렌더링
	 *********************************************************/
	function renderSubHobbies(mainKey) {
		subTitle.style.display = "block";

		subHobbyData[mainKey].forEach(hobby => {
			const label = document.createElement("label");
			const checkbox = document.createElement("input");

			checkbox.type = "checkbox";
			checkbox.value = hobby.id;
			checkbox.checked = selectedHobbyList.includes(hobby.id);

			label.appendChild(checkbox);
			label.append(` ${hobby.name}`);
			categoryArea.appendChild(label);
		});
	}

	/*********************************************************
	 * 초기화
	 *********************************************************/
	function initSelectedHobbies() {
		// hidden 복원
		selectedHobbyList.forEach(id => {
			const hidden = document.createElement("input");
			hidden.type = "hidden";
			hidden.name = "hobbyCode";
			hidden.value = id;
			hiddenHobbyArea.appendChild(hidden);
		});

		// 최초 활성 메인 취미
		Object.entries(subHobbyData).some(([key, list]) => {
			if (list.some(h => selectedHobbyList.includes(h.id))) {
				activeMainHobby = key;
				renderSubHobbies(key);
				return true;
			}
		});

		renderMainHobbies();
		enhanceExistingHobbyTags();
	}

	/*********************************************************
	 * 실행
	 *********************************************************/
	initSelectedHobbies();



	/* 닉네임 유효성 검사 */
	const checkObj = {
		"memberNickname": true
	};


	memberNickname.addEventListener("input", e => {

		const inputNickname = e.target.value; // 입력값 갖고 옴

		// 미입력
		if (inputNickname.trim().length === 0) {
			checkObj.memberNickname = false;
			return;
		}

		// 닉네임 정규식에 맞지 않는 경우
		let regExp = /^[a-zA-Z가-힣0-9]{2,8}$/;

		// 정규식이 아닐 경우
		if (!regExp.test(inputNickname)) {
			checkObj.memberNickname = false;
			return;
		}


		// 닉네임 중복 검사
		fetch("/myPage/checkNickname?memberNickname=" + memberNickname.value)
			.then(resp => resp.text())
			.then(count => {

				if (count == 1) { // 중복인 경우
					checkObj.memberNickname = false;
					return;
				} else {
					checkObj.memberNickname = true;
				}

			});

	})
	// form 태그 제출된 경우
	updateInfo.addEventListener("submit", e => {

		const memberNickname = document.querySelector("#memberNickname");
		const memberTel = document.querySelector("#memberTel");
		const memberAddress = document.querySelectorAll("[name='memberAddress']");

		syncSelectedSubHobbies();

		/*  닉네임 최종 유효성 검사 */
		if (!checkObj.memberNickname) {
			alert("닉네임이 유효하지 않거나 이미 사용 중입니다.");
			memberNickname.focus();
			e.preventDefault(); // 여기서 막아야 실제 전송이 안 됨
			return;
		}

		/* 전화번호 유효성 검사 */

		// 미입력
		if (memberTel.value.trim().length === 0) {
			alert("휴대전화번호를 입력해 주세요.");
			e.preventDefault();
			return;
		}

		// 전화번호가 정규식에 맞지 않는 경우(01*-***(*)-****)
		regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

		if (!regExp.test(memberTel.value)) {
			alert("휴대전화번호가 유효하지 않습니다.");
			e.preventDefault();
			return;
		}


		/* 주소 유효성 검사 */

		// 입력을 안하면 전부 안하고, 입력하면 전부 해야함

		const addr0 = memberAddress[0].value.trim().length == 0; // t/f
		const addr1 = memberAddress[1].value.trim().length == 0; // t/f
		const addr2 = memberAddress[2].value.trim().length == 0; // t/f

		// 모두 true 인 경우만 true 저장
		const result1 = addr0 && addr1 && addr2; // 아무것도 입력하지 않은 경우

		// 모두 false 인 경우만 true 저장
		const result2 = !(addr0 || addr1 || addr2); // 전부 입력한 경우

		// 모두 입력 또는 모두 미입력이 아니면 (=> 유효하지 않은 경우)
		if (!(result1 || result2)) {
			alert("주소를 모두 작성 또는 미작성 해주세요.");
			e.preventDefault();
		}

	});

}


/** 비밀번호 수정 **/

// 비밀번호 변경 form 태그
const changePw = document.querySelector("#changePw");

if (changePw != null) { // 제출 되었을 때(변경 버튼 클릭)

	changePw.addEventListener("submit", e => {

		const currentPw = document.querySelector("#currentPw");
		const newPw = document.querySelector("#newPw");
		const confirmPw = document.querySelector("#confirmPw");

		// - 값을 모두 입력했는가

		let str; // undefined 상태
		if (currentPw.value.trim().length === 0) str = "현재 비밀번호를 입력해주세요";
		else if (newPw.value.trim().length === 0) str = "새 비밀번호를 입력해주세요";
		else if (confirmPw.value.trim().length === 0) str = "새 비밀번호 확인을 입력해주세요";

		if (str != undefined) { // str에 값이 대입됨 == if 중 하나 실행됨
			alert(str);
			e.preventDefault();
			return;
		}

		// 새 비밀번호 정규식
		const regExp = /^(?=.*[!@#_-])[a-zA-Z0-9!@#_-]{6,20}$/; // 6~20 특수문자 최소 1개

		if (!regExp.test(newPw.value)) {
			alert("새 비밀번호가 유효하지 않습니다.");
			e.preventDefault();
			return;
		}

		// 새 비밀번호 == 새 비밀번호 확인
		if (newPw.value != confirmPw.value) {
			alert("새 비밀번호가 일치하지 않습니다.");
			e.preventDefault();
			return;
		}
	});
};


// -------------------------------------
/* 탈퇴 유효성 검사 */

// 탈퇴 form 태그
const secession = document.querySelector("#secession");

if (secession != null) {
	console.log(secession.value);

	secession.addEventListener("submit", e => {

		const memberPw = document.querySelector("#memberPw");
		const agree = document.querySelector("#agree");

		// - 비밀번호 입력 되었는지 확인
		if (memberPw.value.trim().length == 0) {
			alert("비밀번호를 입력해주세요.");
			e.preventDefault(); // 제출막기
			return;
		}

		// 약관 동의 체크 확인
		// checkbox 또는 radio checked 속성
		// - checked -> 체크 시 true, 미체크시 false 반환

		if (!agree.checked) { // 체크 안됐을 때
			alert("약관에 동의해주세요.");
			e.preventDefault();
			return;
		}

		// 정말 탈퇴? 물어보기
		if (!confirm("정말 탈퇴 하시겠습니까?")) {
			alert("취소 되었습니다.");
			e.preventDefault();
			return;
		}
	});
}

// ------------------------ 프로필 이미지 변경 (즉시 업로드) --------------------------

const imageInput = document.querySelector("#imageInput");

// 기존 이미지 주소 저장
const originalProfileImg = document.querySelector("#profileImg").src;

if(imageInput != null) {
	
	imageInput.addEventListener("change", async(e) => {
		
		const file = e.target.files[0];
		if(!file) return;
		
		// 미리보기 먼저 보여주기 (FileReader)
		const reader = new FileReader();
		reader.onload = (e) => {
			document.querySelector("#profileImg").src = e.target.result;
		};
		reader.readAsDataURL(file);
		
		// 로딩 표시
		showLoading(true);
		
		// 서버에 업로드
		const formData = new FormData();
		formData.append("profileImg", file);
		
		try {
			const result = await fetch("/myPage/profile", {
				method: "POST",
				body: formData
			}).then(resp => resp.json());
			
			if(result.success) {
				// 실제 서버 경로로 교체
				document.querySelector("#profileImg").src = result.imagePath;
				
				// Toast 알림
				showToast("프로필 이미지가 변경되었습니다.")
				
				// 이미지 살짝 애니메이션
				const img = document.querySelector("#profileImg");
				img.style.transition = "opacity 0.3s";
				img.style.opacity = "0.5";
				setTimeout(() => img.style.opacity = "1", 300);
				
				
			} else {
				showToast("이미지 업로드에 실패했습니다.");
				// 미리보기 원래대로 되돌리기
				document.querySelector("#profileImg").src = originalProfileImg;
			}
			
		} catch(err) {
			console.log(err);
			showToast("오류가 발생했습니다. 다시 시도해주세요.");
		} finally {
			// 로딩 해제
			showLoading(false);
		}
		
	});
	
}

// ------------------------ 기본 이미지 변경 --------------------------
const defaultImgBtn = document.querySelector("#defaultImgBtn");
const defaultImageUrl = `${window.location.origin}/images/user.png`;

defaultImgBtn.addEventListener("click", async () => {
	const profileImg = document.querySelector("#profileImg");
	
	if(profileImg.src === defaultImageUrl) {
		showToast("이미 기본 이미지입니다.");
		return;
	}
	
	showLoading(true);
	
	try {
		
		// isDefault 파라미터만 전송, 파일 없음
		const formData = new FormData();
		// form-data 형식은 String으로만 전송 가능! => 어떤 타입을 넣어도 문자열로 변환되어 전송
		// 따라서 "true"로 작성하는 것이 의도를 더 명확하게 표현 (둘 다 동작은 동일!)
		formData.append("isDefault", "true") 
		
		const result = await fetch("/myPage/profile", {
			method: "POST",
			body: formData
		}).then(resp => resp.json());
		
		if(result.success) {
			profileImg.src = defaultImageUrl;

			profileImg.style.transition = "opacity 0.3s";
			profileImg.style.opacity = "0.5";
			setTimeout(() => profileImg.style.opacity = "1", 300);

			showToast("기본 이미지로 변경되었습니다.");
		} else {
			showToast("기본 이미지 변경에 실패했습니다.")
		}
		
	} catch(err) {
		console.log(err);
		showToast("오류가 발생했습니다. 다시 시도해주세요.");
	} finally {
		showLoading(false);
	}
	
});

// Toast 함수
function showToast(message) {
    const toast = document.querySelector("#toastMsg");
    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2500); // 2.5초 후 사라짐
}

// 로딩 함수
function showLoading(flag) {
    const loading = document.querySelector("#profileLoading");
    loading.classList.toggle("show", flag);
}






