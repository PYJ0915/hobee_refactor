package hobee.semi.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.service.PenaltyService;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	private PenaltyService service;

	@RequestMapping("/")
	public String mainPage(HttpSession session, Model model) {

	    MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

	    String path = "common/main";

	    if (loginMember != null) {

	        // 경고를 이미 보여줬는지 여부 (세션 기준)
	        Boolean showWarning = (Boolean) session.getAttribute("showWarning");

	        // 아직 한 번도 안 보여줬을 때만 검사
	        if (showWarning == null || !showWarning) {

	            Penalty penalty = service.selectPenalty(loginMember.getMemberNo());

	            if (penalty != null && "WARNING".equals(penalty.getPenaltyType())) {
	                model.addAttribute("penalty", penalty);

	                // 경고를 한 번 보여줬다고 세션에 기록
	                session.setAttribute("showWarning", true);

	                path = "penalty/penalty";
	            }
	        }
	    }

	    return path;
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
