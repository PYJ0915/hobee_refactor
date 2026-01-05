package hobee.semi.project.report.model.service;

import java.util.Map;

import hobee.semi.project.report.model.dto.Report;

public interface ReportService {

	int insertReport(Report insertReport);

	Map<String, Object> selectReportList(int cp);

	int manageReport(Report updateReport);

	Report selectTarget(int reportNo);

}
