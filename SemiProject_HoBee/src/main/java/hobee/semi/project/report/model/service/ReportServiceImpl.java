package hobee.semi.project.report.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.report.model.dto.Report;
import hobee.semi.project.report.model.mapper.ReportMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private ReportMapper mapper;

	@Override
	public int insertReport(Report insertReport) {
		return mapper.insertReport(insertReport);
	}

	@Override
	public List<Report> selectReportList() {
		return mapper.selectReportList();
	}
	
}
