package hobee.semi.project.report.model.service;

import java.util.List;

import hobee.semi.project.report.model.dto.Report;

public interface ReportService {

	int insertReport(Report insertReport);

	List<Report> selectReportList();

}
