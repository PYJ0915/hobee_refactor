package hobee.semi.project.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}
