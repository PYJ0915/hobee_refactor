package hobee.semi.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

	@RequestMapping("/")
	public String mainPage() {
		return "common/main";
	}
	
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용바랍니다.");
		return "redirect:/";
	}
	
	@GetMapping("adminError")
	public String adminError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "관리자만 이용가능한 페이지 입니다.");
		return "redirect:/";
	}
	
}
