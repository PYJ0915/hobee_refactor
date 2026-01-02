package hobee.semi.project.member.controller;



import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("member")
@SessionAttributes({"loginMember"}) // 세션 스코프에 로그인 한 회원정보 저장
public class MemberController {



	private final MemberService service;
	
	
	// 로그인 페이지
	@GetMapping("loginPage")
	public String loginPage() {
		return"member/loginPage";
	}
	
	
	// 로그인 
	@PostMapping("loginPage")
	public String login(@ModelAttribute MemberDTO inputMember/*로그인 창에 쓴 값(그릇) */,
					Model model,/*값을 들고 클라이언트 이동(바구니)*/
					RedirectAttributes ra,
					@RequestParam(value = "saveId", required = false) String saveId/*saveId 값*/,
					HttpServletResponse resp /*쿠기를 클라이언트로 옮기기 위해 */
			) {
		
		try {
			// member 모든 값이 들어가져 있음
			MemberDTO loginMember = service.loginMember(inputMember);
			
			log.debug("로그인 회원 loginMember 상태 : " + loginMember);
			log.debug("체크박스 saveId 상태 : " + saveId);
			
			if(loginMember != null) {
				model.addAttribute("loginMember", loginMember);
				
				// 쿠키 객체 생성(회원 정보 관리하기 위해(입장권 번호))
				Cookie cookie = new Cookie("saveId",loginMember.getMemberId());
				
				// 사이트 모두 가능
				cookie.setPath("/");
				
				if(saveId != null) {// 체크함
					cookie.setMaxAge(60*60*24*30);  // 30일동안 생존 이후 삭제
				}
				else {
					cookie.setMaxAge(0); // 실패 시 0초 생존 
				}
				
				// 클라이언트로 이동
				resp.addCookie(cookie);
				
			}else {
				ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
				return "redirect:/member/loginPage"; // 로그인 실패시 로그인 페이지로 이동
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
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
		return service.checkEmail(memberEmail);}


	// 아이디 중복 검사
	@ResponseBody
	@GetMapping("checkId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkId(memberId);
		
	}
	
	// 닉네임 중복검사
	@ResponseBody
	@GetMapping("checkNickname")
	public int checNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}
	
	// 회원가입
	@PostMapping("signUp")
	public String signUp(MemberDTO inputMember, 
			@RequestParam("memberAddress") List<String> memberAddress,
			@RequestParam(value="hobbyCodes", required=false) List<String> hobbyCodes,
			RedirectAttributes ra) {
		
		
		
		int result = service.signUp(inputMember,memberAddress,hobbyCodes); // 회원가입 정보 모두 들어있음
		
		String path = "";
		String message = "";
		
		// 성공
		if(result >0 ) {
			path="/"; // 메인페이지로 이동
			message=inputMember.getMemberNickname()+"님 가입을 축하합니다.";
		}else {
			path = "signUp"; // 다시 가입 페이지로 
	        message = "회원 가입에 실패했습니다. 다시 시도해주세요.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	


}