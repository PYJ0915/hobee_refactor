package hobee.semi.project.findHobby.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.common.util.Utility;
import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.findHobby.model.dto.Question;
import hobee.semi.project.findHobby.model.dto.QuestionScore;
import hobee.semi.project.findHobby.model.service.FindHobbyService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("findHobby")
@Slf4j
public class FindHobbyController {
	
	@Autowired
	private FindHobbyService service;
	
	@GetMapping("main")
	public String findHobbyMain() {
		return "findHobby/findHobbyStart";
	}
	
	@GetMapping("start")
	public String findHobbyStart() {
		return "findHobby/question";
	}
	
	@ResponseBody
	@GetMapping("selectQuestionList")
	public List<Question> selectQuestionList() {
		return service.selectQuestionList();
	}
	
	@ResponseBody
	@GetMapping("selectScore")
	public List<QuestionScore> selectScore(@RequestParam("questionNo") int questionNo) {
		return service.selectScore(questionNo);
	}
	
	@GetMapping("end")
	public String findHobbyEnd(@RequestParam("firstHobby") String firstHobby, 
							@RequestParam("secondHobby") String secondHobby,
							HttpSession session, Model model,
							RedirectAttributes ra ) {
		
		Map<String, Hobby> hobbyMap = service.getHobby(firstHobby, secondHobby);
		
		String path = null;
		
		if(hobbyMap == null) {
			
			path = "redirect:/findHobby/main";
			ra.addFlashAttribute("message", "예기치 못한 오류로 취미 탐색 검사를 실패했습니다. 다시 시도해주세요.");
			
		} else {
			
			path = "findHobby/findHobbyEnd";
			
			model.addAttribute("firstHobby", hobbyMap.get("firstHobby"));
			model.addAttribute("secondHobby", hobbyMap.get("secondHobby"));
			
			int firstHobbyCode = Utility.getHobbyCode(firstHobby);
			
			String resultMessage = null;
			
			switch (firstHobbyCode) {
			case 1:
				resultMessage = "몸을 움직일 때 가장 나다운 당신은 가만히 있기보다, 움직일 때 에너지가 채워지는 타입이에요.";
				break;
			case 2:
				resultMessage = "느끼고 표현할 때 가장 편안해지는 당신은 결과보다 과정과 감정이 더 중요한 타입이에요.";
				break;
			case 3:
				resultMessage = "배우고 성장할 때 가장 만족스러운 당신은 시간을 쓰더라도, 남는 게 있는 걸 좋아하는 타입이에요.";
				break;
			case 4:
				resultMessage = "사람들과 어울릴 때 가장 즐거운 당신은 함께할수록 에너지가 커지는 타입이에요.";
				break;
			case 5:
				resultMessage = "관심 있는 것을 발견할 때 설레는 당신은 좋아하는 걸 하나씩 쌓아가는 데서 즐거움을 느끼는 타입이에요.";
				break;
			}
			
			model.addAttribute("resultMessage", resultMessage);
			
		}
		
		return path;
	}
	
	
}
