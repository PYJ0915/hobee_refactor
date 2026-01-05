package hobee.semi.project.myPage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.myPage.model.service.MyPageService;

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("myPage")
//@Slf4j
public class MyPageController {

	@Autowired
	private MyPageService service;
	
	/** 회원 정보 조회
	 * @param loginMember
	 * @param model
	 * @return
	 */
	@GetMapping("info")
	public String info(@SessionAttribute("loginMember") MemberDTO loginMember,
					  Model model) {
		
		String memberAddress = loginMember.getMemberAddress();
		
		if(memberAddress != null) {
			
			String[] arr = memberAddress.split("\\^\\^\\^");
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		
		List<Hobby> hobbyList =
			    service.selectHobbyList(loginMember.getHobbyCode());

		model.addAttribute("hobbyList", hobbyList);
		
		return "myPage/myPage-profile";
	}

	/** 회원 정보 수정 화면 이동
	 * @return
	 */
	@GetMapping("updateInfo")
	public String updateInfo() {
		
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
			
			message = "회원 정보가 수정되었습니다.";
			
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			loginMember.setMemberIntroduction(inputMember.getMemberIntroduction());
			loginMember.setHobbyCode(inputMember.getHobbyCode());
		
		} else {
			
			message = "회원 정보 수정 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";
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
			path = "profile";
		
		} else {
			
			message = "기존 비밀번호를 확인해주세요";
			path = "changePw";
		}

		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
						  @SessionAttribute(value="loginMember", required=false) MemberDTO loginMember,
						  RedirectAttributes ra) throws Exception {
		
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) {
			
			message = "프로필 이미지 변경 성공!" ;
			
		}else {
			
			message = "프로필 이미지 변경 실패ㅜㅜ";
			
		}
		
		ra.addFlashAttribute("message",message);
		
		return "redirect:info";
		
	}
	
}










