package hobee.semi.project.penalty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.service.PenaltyService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/penalty")
public class PenaltyController {

	@Autowired
	private PenaltyService penaltyService;

	@GetMapping("/suspend")
	public String suspendPage(HttpSession session, Model model) {

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		// 비로그인 접근 방어
		if (loginMember == null) {
			return "redirect:/";
		}

		Penalty penalty = penaltyService.selectPenalty(loginMember.getMemberNo());

		// 혹시나 정지 아닌 상태면 메인으로
		if (penalty == null || !"SUSPEND".equals(penalty.getPenaltyType())) {
			return "redirect:/";
		}

		model.addAttribute("penalty", penalty);
		return "penalty/penalty";
	}

	@GetMapping("/permanent")
	public String permanentPage(HttpSession session, Model model) {

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		if (loginMember == null) {
			return "redirect:/";
		}

		Penalty penalty = penaltyService.selectPenalty(loginMember.getMemberNo());

		if (penalty == null || !"PERMANENT".equals(penalty.getPenaltyType())) {
			return "redirect:/";
		}

		model.addAttribute("penalty", penalty);
		return "penalty/penalty";
	}

}
