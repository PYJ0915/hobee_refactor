package hobee.semi.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("footer")
public class FooterController {

	@GetMapping("terms")
	public String footerTerms() {
		return "footerLink/terms";
	}
	
	@GetMapping("privacy")
	public String footerPrivacy() {
		return "footerLink/privacy";
	}
	
	@GetMapping("cs")
	public String footerCs() {
		return "footerLink/cs";
	}
	
	@GetMapping("project")
	public String footerproject() {
		return "footerLink/project";
	}
	
}
