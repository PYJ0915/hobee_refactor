# 🐝 HOBEE
### Hobby + Bee — 취미로 연결되는 커뮤니티

> 나에게 맞는 취미를 발견하고, 같은 관심사를 가진 사람들과 소통하며 일상에 새로운 즐거움을 더해보세요

---

> ⚠️ **본 레포지토리는 팀 프로젝트 HOBEE를 개인적으로 리팩토링하는 브랜치입니다.**  
> 원본 팀 프로젝트와 별개로, 코드 품질 개선 및 기능 고도화를 목적으로 운영됩니다.

---

## 📌 프로젝트 소개

**HOBEE**는 취미를 통해 사람과 사람을 연결하는 **커뮤니티 플랫폼**입니다.

취미 적성 검사를 통해 나에게 맞는 취미를 추천받고, 취미·자유·공지 게시판에서 같은 관심사를 가진 사람들과 자유롭게 소통할 수 있습니다. 팔로우, 실시간 채팅, 모임 모집, 챌린지 등 다양한 소셜 기능을 통해 취미 친구를 만들고 함께 성장할 수 있습니다.

---

## 🔨 리팩토링 목표

> 팀 프로젝트에서 아쉬웠던 부분을 개선하고, 더 나은 코드 구조와 사용자 경험을 만드는 것을 목표로 합니다.

- [x] ♻️ 중복 코드 제거 및 코드 스타일 통일
- [x] ⚠️ 예외 처리 통일 및 추가
- [x] 🚀 코드 품질 향상 및 기능 고도화

---

## ✅ 개선 사항

| 분류 | 기존 | 개선 |
|------|------|------|
| 게시판 구조 | FreeBoardController, HobbyBoardController, NoticeBoardController 분리 | BoardController 단일 컨트롤러로 통합, boardCode 파라미터로 분기 |
| 의존성 주입 | `@Autowired` 필드 주입 혼용 | `@RequiredArgsConstructor` + `final` 생성자 주입으로 전체 통일 |
| 동시성 | `static int seqNum` 공유 필드 | UUID를 통한 동시성 보장 |
| 정적 필드 | `static int hobbyCode` 공유 필드 | 로컬 변수로 변경하여 사이드 이펙트 제거 |
| 예외 처리 | `ExceptionController` 에러 정보 없음 | `@Slf4j` 로그 추가, URI 및 스택 트레이스 기록 |
| NPE 방어 | `AdminFilter`에서 loginMember null 체크 없음 | null 체크 후 리다이렉트 처리 |
| 로그인 | try-catch로 전체 로직 감쌈, `e.printStackTrace()` | try-catch 제거 및 `log.error()` 로 교체 |
| 트랜잭션 | 파일 저장 실패 시 DB만 커밋되는 문제 | 파일 저장 실패 시 `RuntimeException` throw로 롤백 보장 |
| 이미지 저장 | 원본 그대로 저장 | Thumbnailator 적용 — 프로필 300×300 크롭, 게시글 최대 1200px 리사이징 |
| 게시판 맵 | `Map.of()` (최대 10개 제한) | `Map.ofEntries()`로 교체 |
| 아코디언 UI | 게시글 목록, 챌린지 목록 분리 표시 | 섹션별 아코디언 구조로 개선, 챌린지별 인증 내역 토글 |

---

## 🆕 추가 기능

### 👥 팔로우 / 알림
- 회원 간 팔로우·언팔로우 (토글 방식)
- 팔로워·팔로잉 수 표시, 클릭 시 목록 모달
- 팔로우, 게시글 등록, 댓글, 모임 확정, 챌린지 달성 시 실시간 알림
- 알림 드롭다운 — 개별 삭제, 모두 읽음, 모두 비우기
- 알림 타입별 클릭 시 해당 페이지로 이동

### 💬 채팅
- **1:1 채팅** — 상대방 프로필에서 바로 채팅 시작
- **단체 채팅** — 채팅방 생성, 회원 초대
- 타이핑 인디케이터, 읽지 않은 메시지 뱃지
- 헤더 채팅 아이콘 — 읽지 않은 메시지 수 표시
- WebSocket(STOMP) 기반 실시간 통신

### 🤝 모임 모집 (boardCode=4)
- 모임 날짜·장소·모집 인원 설정
- 카카오 주소 검색 API + 카카오맵 장소 미리보기
- 참여 신청 → 작성자 수락/거절 → 모임 확정 시 단체 채팅방 자동 생성
- 신청 상태별 UI (대기 중 / 확정 / 거절)
- 게시글 수정 시 기존 모임 정보 미리 입력

### 🏆 챌린지
- 챌린지 생성·목록·상세·참여·인증·마감
- 인증 시 이미지 첨부 가능 (Thumbnailator 리사이징 적용)
- 달성률 프로그레스 바 표시
- `@Scheduled` 자동 마감 (매일 자정)
- 목표 달성 시 알림 발송
- 마이페이지 / 다른 사람 프로필에서 참여 챌린지 + 인증 내역 아코디언으로 확인

### 🖼️ 프로필
- 다른 회원 프로필 페이지 (`/member/profile/{memberNo}`)
- 프로필 이미지 즉시 업로드 (파일 선택 즉시 서버 업로드 + 미리보기)
- 기본 이미지로 변경 기능

---

## ⚙️ 기술 스택

<div align="center">

| 분류 | 기술 |
|:---:|:---|
| **🖥 Front-end** | ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) |
| **🛠 Back-end** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-DC382D?style=for-the-badge&logo=databricks&logoColor=white) ![Oracle](https://img.shields.io/badge/Oracle_DB-F80000?style=for-the-badge&logo=oracle&logoColor=white) ![Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black) |
| **🗃 DB Tool** | ![DBeaver](https://img.shields.io/badge/DBeaver-382923?style=for-the-badge&logo=dbeaver&logoColor=white) |
| **📦 라이브러리** | Thumbnailator 0.4.20 (이미지 리사이징), SummerNote (웹 에디터), Kakao Maps API, SockJS + STOMP (WebSocket) |

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
- **프로필 페이지** : 다른 회원의 프로필·게시글·챌린지 참여 내역 확인, 팔로우·채팅 버튼

### 📋 게시판
- **자유 / 취미 / 공지 / 모임 모집** 게시판 운영 (공지는 관리자 전용 작성)
- SummerNote 웹 에디터 활용 — 서식, 이미지 첨부, Drag & Drop 지원
- 좋아요 기반 TOP 5 랭킹 (좋아요 → 댓글 수 → 조회수 순)
- 조회수·좋아요·작성일 기준 정렬
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

## 🛠️ 트러블슈팅

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
  - 해결: 전역 변수 `reportedMemberNo`를 선언하고, 신고 버튼 클릭 시 해당 번호를 주입하는 방식으로 해결

- **FreeBoardController·HobbyBoardController·NoticeBoardController 중복 코드 문제**
  - 원인: 게시판 종류마다 Controller·Service·Mapper를 별도로 구현하여 동일한 로직이 3벌 존재
  - 해결: `boardCode` 파라미터 기반으로 `BoardController` 단일 컨트롤러로 통합, 코드량 60% 감소

- **파일 저장 실패 시 DB 불일치 문제**
  - 원인: `mapper.profile()` DB INSERT 성공 후 `transferTo()`(파일 저장) 실패 시 `@Transactional`이 롤백되지 않아 DB에는 경로가 저장되지만 실제 파일이 없는 상태 발생
  - 해결: 파일 저장 로직을 try-catch로 감싸고 실패 시 `RuntimeException`을 throw하여 트랜잭션 롤백 보장

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
| `FOLLOW` | 팔로우 관계 |
| `NOTIFICATION` | 알림 내역 |
| `CHAT_ROOM` / `CHAT_ROOM_MEMBER` / `CHAT_MESSAGE` | 채팅방·참여자·메시지 |
| `GATHERING` / `GATHERING_MEMBER` | 모임 모집·참여자 |
| `CHALLENGE` / `CHALLENGE_MEMBER` | 챌린지·참여자 |
| `CERT` | 챌린지 인증 내역 |

---

## 📅 개발 기간

| 단계 | 내용 | 기간 |
|------|------|------|
| 기획·설계 | 요구사항 정의, 와이어프레임, DB 설계 | 2025.12.22 ~ 12.26 |
| 개발 | 기능 구현, 퍼블리싱 | 2025.12.27 ~ 2026.01.07 |
| QA·마무리 | 테스트 및 최종 점검 | 2026.01.08 ~ 01.09 |
| **리팩토링 1차** | **Board 통합, 코드 스타일 정리, 버그 수정** | **2026.03 ~ 04** |
| **리팩토링 2차 + 기능 추가** | **팔로우·알림·채팅·모임모집·이미지리사이징·챌린지** | **2026.05 ~ 06** |

---

## 📁 프로젝트 구조

```
HOBEE
├── Front-end (HTML + CSS + JavaScript)
│   ├── main          # 메인페이지, 헤더, 풋터
│   ├── member        # 회원가입, 로그인, 마이페이지, 프로필
│   ├── findHobby     # 취미 탐색
│   ├── board         # 자유·취미·공지·모임모집 게시판
│   ├── challenge     # 챌린지 목록·상세·생성·인증
│   ├── chat          # 채팅 (1:1 / 단체)
│   └── admin         # 관리자 (신고·제재·문의)
└── Back-end (Spring Boot + Oracle)
    ├── Member        # 회원 관리, 인증
    ├── Hobby         # 취미 탐색
    ├── Board         # 게시판, 댓글
    ├── Gathering     # 모임 모집
    ├── Challenge     # 챌린지, 인증
    ├── Chat          # 실시간 채팅 (WebSocket)
    ├── Follow        # 팔로우
    ├── Notification  # 알림
    ├── Report        # 신고·제재
    └── Admin         # 관리자
```

---

<p align="center">
  <b>🐝 HOBEE</b> — Hobby + Bee, 취미로 연결되는 커뮤니티
</p>
