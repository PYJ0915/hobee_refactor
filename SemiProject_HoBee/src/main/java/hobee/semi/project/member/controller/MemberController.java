package hobee.semi.project.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

	@GetMapping("loginPage")
	public String loginPage() {
		return"member/loginPage";
	}
	
	@PostMapping("loginPage")
	public String login() {
		
		
		return"redirect:/";
	}
}
