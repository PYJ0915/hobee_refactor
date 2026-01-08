package hobee.semi.project.footer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.footer.model.dto.CS;
import hobee.semi.project.footer.model.service.FooterService;
import hobee.semi.project.report.model.dto.Report;

@Controller
@RequestMapping("footer")
public class FooterController {

	@Autowired
	private FooterService service;

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

	@PostMapping("cs")
	public String insertCS(@ModelAttribute CS cs, RedirectAttributes ra) {

		String message = null;

		if (cs.getCsWriterName().equals("")) {
			message = "이름을 입력해주세요.";
		} else if (cs.getCsWriterEmail().equals("")) {
			message = "이메일을 입력해주세요.";
		} else if (cs.getCsContent().equals("")) {
			message = "문의 내용을 입력해주세요";
		} else {

			int result = service.insertCS(cs);

			if (result > 0) {
				message = "문의가 정상적으로 접수되었습니다.";
			} else {
				message = "문의 접수에 실패했습니다. 잠시 후 다시 시도해 주세요.";
			}

		}

		ra.addFlashAttribute("message", message);

		return "redirect:cs";

	}

	@GetMapping("manageCS")
	public String manageCS(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model) {

		Map<String, Object> map = null;

		map = service.selectCSList(cp);

		model.addAttribute("csList", map.get("csList"));
		model.addAttribute("pagination", map.get("pagination"));

		return "footerLink/manage-cs";
	}
	
	@ResponseBody
	@GetMapping("selectTarget")
	public CS selectTarget(@RequestParam("csNo") int csNo) {
		return service.selectTarget(csNo);
	}
	
	@PutMapping("csComplete")
	@ResponseBody
	public int csComplete(@RequestBody int csNo) {
		return service.csComplete(csNo);
	}

}
