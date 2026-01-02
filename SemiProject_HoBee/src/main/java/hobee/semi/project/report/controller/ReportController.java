package hobee.semi.project.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hobee.semi.project.report.model.dto.Report;
import hobee.semi.project.report.model.service.ReportService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("report")
@Slf4j
public class ReportController {
	
	@Autowired
	private ReportService service;
	
	@PostMapping("insertReport")
	@ResponseBody
	public int insertReport(@RequestBody Report insertReport) {
		
		log.info("비동기로 넘어온 Report : " + insertReport);
		
		return service.insertReport(insertReport);
	}
	
	@GetMapping("manageReport")
	public String manageReport(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, 
							Model model) {
		
		Map<String, Object> map = null;
		
		map = service.selectReportList(cp);
		
		model.addAttribute("reportList", map.get("reportList"));
		model.addAttribute("pagination", map.get("pagination"));
		
		return "report/manage-report";
	}
	
	@PutMapping("manageReport")
	@ResponseBody
	public int manageReport(@RequestBody Report updateReport) {
		log.info("updateReport : " + updateReport);
		return service.manageReport(updateReport);
	}
	
}
