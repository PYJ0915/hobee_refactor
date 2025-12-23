package hobee.semi.project.findHobby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("findHobby")
public class FindHobbyController {
	
	@GetMapping("main")
	public String findHobbyMain() {
		return "findHobby/findHobbyStart";
	}
	
	@GetMapping("start")
	public String findHobbyStart(@RequestParam("inputName") String inputName, HttpSession session) {
		session.setAttribute("inputName", inputName);
		return "findHobby/question";
	}
	
	
}
