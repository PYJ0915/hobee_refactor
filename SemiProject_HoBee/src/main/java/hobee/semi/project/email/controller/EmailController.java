package hobee.semi.project.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import hobee.semi.project.email.model.dto.Email;
import hobee.semi.project.email.model.service.EmailService;

@Controller
@RequestMapping("email")
public class EmailController {
	
	@Autowired
	private EmailService service;

	// 이메일에 인증번호 보내기
	@ResponseBody
	@PostMapping("signup")
	public int signup(@RequestBody Email email) {
		
		String authEmail = email.getAuthEmail();
		
		return service.checkEmail ("signup",authEmail);
		
	}
	
	// 인증번호 확인
	@ResponseBody
	@PostMapping("checkAuthKey")
	public int checkAuthKey(@RequestBody Email email) {
		
		String authKey = email.getAuthKey();
		String authEmail = email.getAuthEmail();
		
		return service.checkAuthKey(authKey,authEmail);
	}
}
