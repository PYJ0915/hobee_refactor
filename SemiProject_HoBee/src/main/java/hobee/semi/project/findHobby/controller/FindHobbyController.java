package hobee.semi.project.findHobby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("findHobby")
public class FindHobbyController {
	
	@GetMapping("start")
	public String findHobbyStart() {
		return "findHobby/question1";
	}
	
	
}
