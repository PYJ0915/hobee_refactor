package hobee.semi.project.member.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.member.model.service.MemberService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("member")
@SessionAttributes("{loginMember}") // 세션 스코프에 로그인 한 회원정보 저장
public class MemberController {

	@GetMapping("loginPage")
	public String loginPage() {
		return"member/loginPage";
	}
	
	private final MemberService service;
	
	@PostMapping("loginPage")
	public String login(@ModelAttribute MemberDTO inputMember/*로그인 창에 쓴 값(그릇) */,
					Model model,/*값들을 들고 클라이언트 이동(바구니)*/
					RedirectAttributes ra
			) {
		
		try {
			
			// member 모든 값이 들어가져 있음
			MemberDTO loginMember = service.loginMember(inputMember);
			
			if(loginMember == null) {
				ra.addFlashAttribute("message","로그인 실패");
			}else {
				ra.addFlashAttribute("loginMember", loginMember);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return"redirect:/";
	}
}