# 🐝 HOBEE
### Hobby + Bee — 취미로 연결되는 커뮤니티

> 나에게 맞는 취미를 발견하고, 같은 관심사를 가진 사람들과 소통하며 일상에 새로운 즐거움을 더해보세요

---

## 📌 프로젝트 소개

**HOBEE**는 취미를 통해 사람과 사람을 연결하는 **커뮤니티 플랫폼**입니다.

취미 적성 검사를 통해 나에게 맞는 취미를 추천받고, 취미·자유·공지 게시판에서 같은 관심사를 가진 사람들과 자유롭게 소통할 수 있습니다. 신고 및 제재 시스템을 통해 건강한 커뮤니티 환경을 유지합니다.

---

## 👥 팀원 소개

| 박상민 | 박유진 | 박세현 | 양충모 | 전주영 |
|:---:|:---:|:---:|:---:|:---:|
| 마이페이지 | 취미 탐색 | 메인화면 | 로그인 | 게시판 화면 구현 |
| 회원 정보 조회·수정 | 게시글 상세 페이지 | 회원 탈퇴 | 로그아웃 | (공지·취미·자유 게시판) |
| 비밀번호 변경 | (좋아요·조회수) | 프로필 변경 | 회원가입 | 게시판 기능 구현 |
| 게시판 댓글 | 신고 모달 구현 | 헤더 | 아이디 찾기 | (페이지 이동·검색·내 글·랭킹) |
| (댓글·대댓글 조회·작성 | 신고 누적 제재 구현 | 풋터 | 비밀번호 찾기 | 게시글 작성 (SummerNote) |
| 등록·수정·삭제·신고) | 신고·제재·문의 관리 페이지 | | | 게시글 수정·삭제 |
| | 로그인 및 관리자 필터 | | | |
| | 이미지 스케줄링 | | | |

---

## ⚙️ 기술 스택

<div align="center">

| 분류 | 기술 |
|:---:|:---|
| **🖥 Front-end** | ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) |
| **🛠 Back-end** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-DC382D?style=for-the-badge&logo=databricks&logoColor=white) ![Oracle](https://img.shields.io/badge/Oracle_DB-F80000?style=for-the-badge&logo=oracle&logoColor=white) ![Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black) |
| **🗃 DB Tool** | ![DBeaver](https://img.shields.io/badge/DBeaver-382923?style=for-the-badge&logo=dbeaver&logoColor=white) |
| **🤝 협업** | ![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white) ![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white) |

</div>

---

## 🗂️ 주요 기능

### 🎯 취미 탐색
- 적성 검사(10문항)를 통한 맞춤 취미 추천
- 검사 결과를 회원 정보에 바로 반영 가능

### 👤 회원 관리
- **회원가입** : 이메일 인증, 아이디·비밀번호 유효성 검사, 취미 복수 선택, 주소 API 연동
- **로그인/로그아웃** : 아이디 저장(쿠키), 세션 기반 인증
- **아이디/비밀번호 찾기** : 이메일 인증 기반 계정 복구
- **마이페이지** : 회원 정보 조회·수정, 비밀번호 변경, 프로필 이미지 관리, 회원 탈퇴

### 📋 게시판
- **자유 / 취미 / 공지** 게시판 분리 운영 (공지는 관리자 전용 작성)
- SummerNote 웹 에디터 활용 — 서식, 이미지 첨부, Drag & Drop 지원
- 좋아요 기반 TOP 5 랭킹 (좋아요 → 댓글 수 → 조회수 순)
- 제목 / 내용 / 제목+내용 / 작성자 검색
- 페이지네이션, 내 글 보기 필터
- 댓글 및 대댓글 — 조회·작성·수정·삭제·신고

### 🚨 신고 및 제재
- 게시글·댓글 신고 (사유 선택 + 추가 사유 입력)
- 하루 신고 한도 5회 제한, 자기 자신·관리자 계정 신고 제한
- 신고 누적 자동 제재 시스템

| 제재 유형 | 조건 | 처리 |
|-----------|------|------|
| **경고** | 승인된 신고 5회 | 접속 시 1회 알림 |
| **일시 이용 제한** | 승인된 신고 10회 | 글·댓글 작성·수정·삭제 차단, 조회만 허용 |
| **영구 정지** | 일시 제한 4회 후 신고 10회 누적 | 고객센터 외 모든 기능 차단 |

### 🔧 관리자
- 신고 관리 — 승인/거절, 신고된 콘텐츠 미리보기 모달
- 제재 회원 관리 — 제재 해제 기능
- 문의 확인 및 처리 완료 처리

---

## 🎨 디자인 시스템

| 항목 | 내용 |
|------|------|
| **Main Color** | `#FFF57E` (Yellow) / `#452829` (Dark Brown) |
| **Font** | 프리텐다드 (눈누 — 길형진 / orioncactus) |
| **Logo** | 벌(Bee) 모티프의 HOBEE 심볼 |

---

## 🛠️ 트러블슈팅

### 박세현
- **프로필 이미지 변경 시 INSERT/UPDATE 분기 처리**
  - 원인: 최초 등록 시에는 INSERT, 이미 이미지가 있을 경우 UPDATE가 필요한 구조
  - 해결: Oracle `MERGE INTO` 구문 활용 — `WHEN MATCHED THEN UPDATE`, `WHEN NOT MATCHED THEN INSERT`로 단일 쿼리 처리

### 박유진
- **10개의 질문 HTML 파일 코드 중복 문제**
  - 원인: 각 질문을 별도 HTML로 구성하여 중복 코드 과다 발생
  - 해결: DB 테이블(`QUESTION`)에 질문 데이터를 저장하고 동적으로 렌더링하여 단일 페이지로 통합

- **점수 10배 이벤트 오류 (배열 순회 문제)**
  - 원인: `forEach`로 버튼 배열을 순회할 때 인덱스가 클로저에 의해 고정되지 않아 오류 발생
  - 해결: `forEach` 대신 인덱스 변수를 직접 증가시키는 방식으로 변경

- **점수가 Map의 키 값으로 사용되어 동점 시 덮어쓰기 발생**
  - 원인: `Map<점수, 취미명>` 구조에서 동점 취미가 존재할 경우 하나만 남는 문제
  - 해결: `Map` 대신 `{ hobby, score }` 형태의 객체 배열로 변경 후 정렬

- **게시글·댓글 신고가 동일 모달을 공유하는 문제**
  - 원인: 신고 모달이 하나이므로 게시글과 댓글의 피신고자 회원 번호를 구분해야 함
  - 해결1 (시도): 닉네임으로 비동기 API 호출하여 회원 번호를 조회하는 함수 구현
  - 해결2 (채택): 전역 변수 `reportedMemberNo`를 선언하고, 신고 버튼 클릭 시 해당 번호를 주입하는 방식으로 해결

### 박상민
- **기존에 선택된 취미 태그 취소 기능 미구현**
  - 원인: 취미 선택 시 태그가 생성되지만, 선택 취소(삭제) 기능이 없어 수정 불가
  - 해결: 각 태그에 `x` 버튼을 동적 생성하고, 클릭 시 상태(selectedHobbyList)·hidden input·체크박스·DOM 요소를 모두 제거하는 `removeHobbyById` 함수 구현

### 양충모
- **카테고리 전환 시 이전 선택 취미 초기화 문제**
  - 원인: 라디오 버튼으로 대분류 전환 시 하위 취미 목록이 재렌더링되어 이전 선택값 소실
  - 해결: 선택된 취미 코드를 `Set`에 누적 관리하고, 폼 제출 시 `Set`의 값을 `hidden input`으로 동적 생성하여 서버 전송

### 전주영
- **SummerNote 글 내용 없이 등록되는 문제**
  - 원인: SummerNote는 빈 상태에서도 `<p></p>` 같은 빈 태그가 존재하여 유효성 검사를 통과
  - 해결: `.replace(/<[^>]*>?/gm, '').trim()`으로 태그를 모두 제거한 순수 텍스트 기준으로 빈 값 체크, 이후 `.val(content)`로 textarea에 반영하여 제출

---

## 📊 ERD

> 프로젝트의 전체 데이터베이스 구조는 ERD Cloud를 통해 설계되었습니다.

**주요 테이블**

| 테이블 | 설명 |
|--------|------|
| `MEMBER` | 회원 정보 |
| `HOBBY` / `MEMBER_HOBBY` | 취미 목록 및 회원-취미 매핑 |
| `BOARD` / `BOARD_TYPE` / `BOARD_CATEGORY` | 게시글 및 게시판 분류 |
| `BOARD_LIKE` | 게시글 좋아요 |
| `COMMENT` | 댓글 및 대댓글 |
| `REPORT` | 신고 내역 |
| `PENALTY` | 제재 내역 |
| `PROFILE_IMG` | 프로필 이미지 |
| `AUTH_KEY` | 이메일 인증키 |
| `FIND_HOBBY` / `QUESTION` | 취미 탐색 질문 및 결과 |
| `CS` | 고객 문의 |

---

## 📅 개발 기간

| 단계 | 내용 | 기간 |
|------|------|------|
| 기획·설계 | 요구사항 정의, 와이어프레임, DB 설계 | 2025.12.22 ~ 12.26 |
| 개발 | 기능 구현, 퍼블리싱 | 2025.12.27 ~ 2026.01.07 |
| QA·마무리 | 테스트 및 최종 점검 | 2026.01.08 ~ 01.09 |

---

## 📁 프로젝트 구조

```
HOBEE
├── Front-end (HTML + CSS + JavaScript)
│   ├── main          # 메인페이지, 헤더, 풋터
│   ├── member        # 회원가입, 로그인, 마이페이지
│   ├── findHobby     # 취미 탐색
│   ├── board         # 자유·취미·공지 게시판
│   └── admin         # 관리자 (신고·제재·문의)
└── Back-end (Spring Boot + Oracle)
    ├── Member        # 회원 관리, 인증
    ├── Hobby         # 취미 탐색
    ├── Board         # 게시판, 댓글
    ├── Report        # 신고·제재
    └── Admin         # 관리자
```

---

## 🔗 팀원 연락처

| 이름 | 이메일 | GitHub |
|------|--------|--------|
| 박상민 | naughtyhoo17@gmail.com | [naughtyhoo17-rgb](https://github.com/naughtyhoo17-rgb) |
| 박유진 | qkrdbwls3542@gmail.com | [PYJ0915](https://github.com/PYJ0915) |
| 박세현 | bagsehyeon582@gmail.com | [sehyeon1689](https://github.com/sehyeon1689) |
| 양충모 | ycm93277211@gmail.com | [ycm93277211-ai](https://github.com/ycm93277211-ai) |
| 전주영 | boduni20@gmail.com | [juyoung-97](https://github.com/juyoung-97) |

---

<p align="center">
  <b>🐝 HOBEE</b> — Hobby + Bee, 취미로 연결되는 커뮤니티
</p>
