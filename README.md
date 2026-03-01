# 🐝 HOBEE - 취미 커뮤니티 플랫폼

> **Ho-bee**는 취미를 통해 사람과 사람을 연결하는 커뮤니티 플랫폼입니다.  
> 나에게 맞는 취미를 발견하고, 같은 관심사를 가진 사람들과 소통하며 일상에 새로운 즐거움을 더해보세요.

---

## 📅 개발 기간

**2025.12.22 ~ 2026.01.09**

---

## 👥 팀원 소개

| 이름 | 담당 기능 | GitHub |
|------|-----------|--------|
| **박상민** | 마이페이지(회원 정보 조회·수정·비밀번호 변경), 게시판 댓글(댓글·대댓글 조회·작성·등록·수정·삭제·신고) | [naughtyhoo17-rgb](https://github.com/naughtyhoo17-rgb) |
| **박유진** | 취미 탐색, 게시글 상세 페이지(좋아요·조회수), 신고 모달, 신고 누적 제재, 신고 관리 페이지, 제재 회원 관리 페이지, 문의 확인 페이지, 로그인 및 관리자 필터, 이미지 스케줄링 | [PYJ0915](https://github.com/PYJ0915) |
| **박세현** | 메인화면, 회원 탈퇴, 프로필 변경, 헤더, 풋터 | [sehyeon1689](https://github.com/sehyeon1689) |
| **양충모** | 로그인, 로그아웃, 회원가입, 아이디 찾기, 비밀번호 찾기 | [ycm93277211-ai](https://github.com/ycm93277211-ai) |
| **전주영** | 게시판 화면 구현(공지·취미·자유), 게시판 기능(페이지 이동·검색·내 글·랭킹), 게시글 작성(SummerNote), 게시글 수정/삭제 | [juyoung-97](https://github.com/juyoung-97) |

---

## 🛠️ 개발 환경

### Language
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white)

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=flat&logo=apache-tomcat&logoColor=black)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=flat&logo=oracle&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=flat)
![DBeaver](https://img.shields.io/badge/DBeaver-382923?style=flat&logo=dbeaver&logoColor=white)

### 협업 도구
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white)
![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white)

---

## 🎨 디자인 시스템

| 항목 | 내용 |
|------|------|
| **Main Color** | `#FFF57E` (Yellow), `#452829` (Dark Brown) |
| **Font** | 프리텐다드 (눈누 - 길형진 / orioncactus) |
| **Logo** | 벌(Bee) 모티프의 HOBEE 심볼 |

---

## ✨ 주요 기능

### 👤 회원 관리
- **회원가입** : 이메일 인증, 아이디/비밀번호 유효성 검사, 취미 선택(복수), 주소 API 연동
- **로그인/로그아웃** : 아이디 저장(쿠키), 세션 관리
- **아이디/비밀번호 찾기** : 이메일 인증 기반 계정 복구
- **마이페이지** : 회원 정보 조회·수정, 비밀번호 변경, 프로필 이미지 관리, 회원 탈퇴

### 🎯 취미 탐색
- 적성 검사(10문항)를 통한 취미 추천
- 검사 결과를 회원 정보에 반영 가능

### 📋 게시판
- **자유 게시판 / 취미 게시판 / 공지 게시판** 분리 운영
- SummerNote 웹 에디터를 활용한 서식 있는 글 작성 (이미지 첨부, Drag & Drop)
- 좋아요 기반 TOP 5 랭킹 기능 (좋아요 → 댓글 수 → 조회수 순)
- 제목 / 내용 / 제목+내용 / 작성자 검색
- 페이지네이션, 내 글 필터
- 댓글 및 대댓글 (등록·수정·삭제·신고)

### 🚨 신고 및 제재
- 게시글/댓글 신고 (사유 선택 + 추가 사유 입력)
- 하루 신고 한도 5회 제한
- 신고 누적 시 자동 제재: **경고(5회) → 일시 이용 제한(10회) → 영구 정지**

### 🔧 관리자
- 신고 관리 (승인/거절, 신고된 콘텐츠 미리보기)
- 제재 회원 관리 (제재 해제)
- 문의 확인 및 처리 완료 처리
- 공지 게시판 단독 작성 권한

---

## 📁 ERD 주요 테이블

| 테이블 | 설명 |
|--------|------|
| `MEMBER` | 회원 정보 |
| `HOBBY` | 취미 목록 |
| `MEMBER_HOBBY` | 회원-취미 매핑 |
| `BOARD` | 게시글 |
| `BOARD_TYPE` | 게시판 종류 |
| `BOARD_CATEGORY` | 게시판 카테고리 |
| `BOARD_LIKE` | 게시글 좋아요 |
| `COMMENT` | 댓글(대댓글 포함) |
| `REPORT` | 신고 내역 |
| `PENALTY` | 제재 내역 |
| `PROFILE_IMG` | 프로필 이미지 |
| `AUTH_KEY` | 이메일 인증키 |
| `FIND_HOBBY` | 취미 탐색 질문-결과 |
| `CS` | 고객 문의 |

---

## 🔗 팀원 연락처

| 이름 | 이메일 | 연락처 |
|------|--------|--------|
| 박상민 | naughtyhoo17@gmail.com | 010-8942-4290 |
| 박유진 | qkrdbwls3542@gmail.com | 010-2340-3542 |
| 박세현 | bagsehyeon582@gmail.com | 010-7564-1689 |
| 양충모 | ycm93277211@gmail.com | 010-9327-7211 |
| 전주영 | boduni20@gmail.com | 010-2186-8825 |
