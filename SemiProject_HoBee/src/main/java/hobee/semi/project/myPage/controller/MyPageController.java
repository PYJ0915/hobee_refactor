package hobee.semi.project.myPage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("myPage")
@Slf4j
@RequiredArgsConstructor
public class MyPageController {

	private final MyPageService service;
	
	private final MemberService memberService;
	
	private final FollowService followService;
	
	private final ChallengeService challengeService;
	
	/** 회원 정보 조회
	 * @param loginMember
	 * @param model
	 * @return
	 */
	@GetMapping("info")
	public String info(@SessionAttribute("loginMember") MemberDTO loginMember,
					  Model model) {
		
		List<Hobby> hobbyList =
			    service.selectHobbyList(loginMember.getHobbyCode());

		loginMember.setHobbyList(hobbyList);
		
		// 게시글 목록 조회
		List<Board> boardList = memberService.selectMemberBoardList(loginMember.getMemberNo());
		
		// 팔로우 관련 데이터
	    int followerCount  = followService.getFollowerCount(loginMember.getMemberNo());
	    int followingCount = followService.getFollowingCount(loginMember.getMemberNo());
	    
		List<Challenge> myChallengeList = challengeService.getMyChallenges(loginMember.getMemberNo());
		
		model.addAttribute("hobbyList", hobbyList);
		model.addAttribute("boardList", boardList);
		model.addAttribute("followerCount", followerCount);
	    model.addAttribute("followingCount", followingCount);
	    model.addAttribute("myChallengeList", myChallengeList);
		
		return "myPage/myPage-profile";
	}

	/** 회원 정보 수정 화면 이동
	 * @return
	 */
	@GetMapping("updateInfo")
	public String updateInfo(@SessionAttribute("loginMember") MemberDTO loginMember,
			  				Model model) {
		
		String memberAddress = loginMember.getMemberAddress();
		
		if(memberAddress != null) {
			
			String[] arr = memberAddress.split("\\^\\^\\^");
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			
			if(arr.length > 2) { // 상세주소가 있을 때만 값을 보내기 위함
				model.addAttribute("detailAddress", arr[2]);
			}
			
		}
		
		return "myPage/myPage-info";
	}
	
	
	/** 회원 정보 수정
	 * @param inputMember
	 * @param memberAddress
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("updateInfo")
	public String updateInfo(@ModelAttribute MemberDTO inputMember,
							@RequestParam("memberAddress") String[] memberAddress,
							@SessionAttribute("loginMember") MemberDTO loginMember,
							RedirectAttributes ra) {
		
		inputMember.setMemberNo(loginMember.getMemberNo());
		
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		
		if(result > 0) {
			
			message = "회원 정보가 수정이 완료되었습니다.";
			
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			loginMember.setMemberIntroduction(inputMember.getMemberIntroduction());
			loginMember.setHobbyCode(inputMember.getHobbyCode());
		
		} else {
			
			message = "회원 정보 수정에 실패했습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";
	}
	
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname,
							@SessionAttribute("loginMember") MemberDTO loginMember) {
		
		int memberNo = loginMember.getMemberNo();
		
		return service.checkNickname(memberNickname, memberNo);
	}
	
	/** 비밀번호 변경 화면 이동
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		
		return "myPage/myPage-changePw";
	}
	
	/** 비밀번호 변경
	 * @param newPw
	 * @param currentPw
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(@RequestParam("newPw") String newPw,
						   @RequestParam("currentPw") String currentPw,
						   @SessionAttribute("loginMember") MemberDTO loginMember,
						   RedirectAttributes ra) {
		
		int result = service.changePw(newPw, currentPw, loginMember);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			
			message = "비밀번호가 변경되었습니다";
			path = "info"; 
		
		} else {
			
			message = "기존 비밀번호를 확인해주세요";
			path = "changePw";
		}

		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	@ResponseBody
	@PostMapping("profile")
	public Map<String, Object> profile(
						@RequestParam(value = "profileImg", required = false) MultipartFile profileImg,
				        @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault,
						@SessionAttribute("loginMember") MemberDTO loginMember) throws Exception {
		
		Map<String, Object> result = new HashMap<>();
		
		int updateResult = service.profile(profileImg, isDefault, loginMember);
		
		if(updateResult > 0) {
			result.put("success", true);
			result.put("imagePath", loginMember.getProfileImg());
		}else {
			result.put("success", false);
		}
		return result;
	}
	
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}
	
	@PostMapping("secession")
	public String secesstion(@RequestParam("memberPw") String memberPw, 
			@SessionAttribute("loginMember") MemberDTO loginMember,
			SessionStatus status, 
			RedirectAttributes ra) {
		
		int memberNo = loginMember.getMemberNo();
		
		int result = service.secession(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "회원 탈퇴가 완료되었습니다.";
			path = "/";
			
			status.setComplete(); // 세션 비우기(로그아웃 상태 변경)
			
		} else {
			
			message = "비밀번호 일치하지 않습니다";
			path = "secession";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:"+ path;
	}
	
}
