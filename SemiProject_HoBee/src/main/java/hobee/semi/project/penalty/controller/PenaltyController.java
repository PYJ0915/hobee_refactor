package hobee.semi.project.penalty.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.service.PenaltyService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("penalty")
@Slf4j
public class PenaltyController {

	@Autowired
	private PenaltyService service;

	@GetMapping("suspend")
	public String suspendPage(HttpSession session, Model model) {

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		// 비로그인 접근 방어
		if (loginMember == null) {
			return "redirect:/";
		}

		Penalty penalty = service.selectPenalty(loginMember.getMemberNo());

		// 혹시나 정지 아닌 상태면 메인으로
		if (penalty == null || !"SUSPEND".equals(penalty.getPenaltyType())) {
			return "redirect:/";
		}

		model.addAttribute("penalty", penalty);
		
		log.info("penalty : " + penalty);
		
		return "penalty/penalty";
	}

	@GetMapping("permanent")
	public String permanentPage(HttpSession session, Model model) {

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		if (loginMember == null) {
			return "redirect:/";
		}

		Penalty penalty = service.selectPenalty(loginMember.getMemberNo());

		if (penalty == null || !"PERMANENT".equals(penalty.getPenaltyType())) {
			return "redirect:/";
		}

		model.addAttribute("penalty", penalty);
		return "penalty/penalty";
	}
	
	@GetMapping("managePenalty")
	public String managePenalty(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
							Model model) {

		Map<String, Object> map = null;
		
		map = service.selectPenaltyList(cp);
		
		model.addAttribute("penaltyList", map.get("penaltyList"));
		model.addAttribute("pagination", map.get("pagination"));
			
		return "penalty/manage-penalty";
	}
	
	@ResponseBody
	@PostMapping("managePenalty")
	public int menagePenalty(@RequestBody int penaltyNo) {
		return service.managePenalty(penaltyNo);
	}

}
