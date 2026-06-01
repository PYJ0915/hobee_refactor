package hobee.semi.project.member.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.challenge.model.dto.Challenge;
import hobee.semi.project.challenge.model.service.ChallengeService;
import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.follow.model.service.FollowService;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.member.model.service.MemberService;
import hobee.semi.project.myPage.model.service.MyPageService;
import hobee.semi.project.profileImg.model.dto.ProfileDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("member")
@SessionAttributes({ "loginMember" }) // 세션 스코프에 로그인한 회원정보 저장
public class MemberController {

	private final MemberService service;
	private final MyPageService myPageService;
	private final FollowService followService;
	private final ChallengeService challengeService;

	// 로그인 페이지
	@GetMapping("loginPage")
	public String loginPage() {
		return "member/loginPage";
	}

	// 로그인
	@PostMapping("loginPage")
	public String login(@ModelAttribute MemberDTO inputMember, Model model, RedirectAttributes ra,
			@RequestParam(value = "saveId", required = false) String saveId, HttpServletResponse resp) {

		MemberDTO loginMember = service.loginMember(inputMember);

		if (loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "redirect:/member/loginPage";
		}

		model.addAttribute("loginMember", loginMember);

		Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
		cookie.setPath("/");
		cookie.setMaxAge(saveId != null ? 60 * 60 * 24 * 30 : 0);

		resp.addCookie(cookie);

		log.info("로그인 성공 : {}", loginMember.getMemberId());

		return "redirect:/";
	}

	// 로그아웃
	@GetMapping("logout")
	public String logout(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "redirect:/";
	}

	// 회원 가입 페이지
	@GetMapping("signupPage")
	public String signup() {
		return "member/signupPage";
	}

	// 이메일 중복 검사
	@ResponseBody
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}

	// 아이디 중복 검사
	@ResponseBody
	@GetMapping("checkId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);

	}

	// 닉네임 중복검사
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}

	// 회원가입
	@PostMapping("signUp")
	public String signUp(MemberDTO inputMember, @RequestParam("memberAddress") String[] memberAddress,
			@RequestParam(value = "hobbyCode", required = false) List<String> hobbyCode, RedirectAttributes ra) {

		log.info("memberAddress : " + memberAddress);

		int result = service.signUp(inputMember, memberAddress, hobbyCode); // 회원가입 정보 모두 들어있음

		String path = "";
		String message = "";

		// 성공
		if (result > 0) {
			path = "/"; // 메인페이지로 이동
			message = inputMember.getMemberNickname() + "님 가입을 축하합니다.";
		} else {
			path = "signUp"; // 다시 가입 페이지로
			message = "회원 가입에 실패했습니다. 다시 시도해주세요.";
		}

		log.debug("회원가입시 hobbyCode 상태 : " + hobbyCode);

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;

	}

	// -------------------------------------------------------------------------------------

	// 아이디 찾기 페이지
	@GetMapping("idSearch")
	public String idSearchPage() {
		return "member/idSearch";
	}

	// 가입된 이름 찾기
	@ResponseBody
	@PostMapping("checkName")
	public int checkName(@RequestBody MemberDTO inputMember) {
		return service.checkName(inputMember);
	}

	// 가입된 이름 찾기
	@ResponseBody
	@PostMapping("checkTel")
	public int checkTel(@RequestBody MemberDTO inputMember) {
		return service.checkTel(inputMember);
	}

	// 아이디 찾기
	@PostMapping("idSearch")
	public String idSearch(MemberDTO inputMember, Model model, RedirectAttributes ra) {

		String foundId = service.findId(inputMember);

		if (foundId == null) {
			ra.addFlashAttribute("message", "일치하는 회원 정보가 없습니다.");
			return "redirect:idSearch"; // 실패 시 다시 찾기 페이지로
		}

		model.addAttribute("foundId", foundId); // 타임리프 사용하기 위해 뿌려 주기

		return "member/idSearchResult"; // 결과 페이지로 이동

	}

	// 비밀번호 찾기 페이지
	@GetMapping("pwSearch")
	public String pwSearchPage() {
		return "member/pwSearch";
	}

	// 새 비밀번호
	@PostMapping("pwSearch")
	public String pwChange(MemberDTO inputMember, RedirectAttributes ra) {

		int result = service.pwChange(inputMember);

		String message = null;
		String path = null;

		if (result > 0) {
			message = "비밀번호가 변경되었습니다.";
			path = "redirect:/member/loginPage"; // 로그인 페이지로
		} else {
			message = "비밀번호 변경에 실패했습니다.";
			path = "redirect:/member/pwSearch"; // 현재 페이지로
		}

		ra.addFlashAttribute("message", message);

		return path;
	}

	@GetMapping("profile/{memberNo:[0-9]+}")
	public String memberProfile(@PathVariable("memberNo") int memberNo,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember, Model model,
			RedirectAttributes ra) {

		// 조회할 회원 정보 가져오기
		MemberDTO targetMember = service.selectMemberProfile(memberNo);

		if (targetMember == null) {
			ra.addFlashAttribute("message", "존재하지 않거나 탈퇴한 회원입니다.");
			return "redirect:/";
		}

		// 취미 코드 조회 → 취미 이름 목록 조회
		List<String> hobbyCodes = service.selectMemberHobbyCode(memberNo);
		targetMember.setHobbyCode(hobbyCodes);

		List<Hobby> hobbyList = myPageService.selectHobbyList(hobbyCodes);
		targetMember.setHobbyList(hobbyList);

		// 게시글 목록 조회
		List<Board> boardList = service.selectMemberBoardList(memberNo);

		// 내 프로필인지 여부 (수정 버튼 / 팔로우 버튼 분기용)
		boolean isMyProfile = loginMember != null && loginMember.getMemberNo() == memberNo;

		// 팔로우 관련 데이터
		int followerCount = followService.getFollowerCount(memberNo);
		int followingCount = followService.getFollowingCount(memberNo);
		boolean isFollowing = loginMember != null && !isMyProfile
				&& followService.isFollowing(loginMember.getMemberNo(), memberNo);

		List<Challenge> challengeList = challengeService.getMyChallenges(memberNo);

		model.addAttribute("myChallengeList", challengeList);
		model.addAttribute("targetMember", targetMember);
		model.addAttribute("boardList", boardList);
		model.addAttribute("isMyProfile", isMyProfile);
		model.addAttribute("followerCount", followerCount);
		model.addAttribute("followingCount", followingCount);
		model.addAttribute("isFollowing", isFollowing);

		return "member/memberProfile";
	}

	// 단체 채팅방 참여자 검색
	@ResponseBody
	@GetMapping("search")
	public List<MemberDTO> searchMembers(@RequestParam("keyword") String keyword) {
		return service.searchMembers(keyword);
	}

}