package hobee.semi.project.report.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Pagination;
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
	public Map<String, Object> selectReportList(int cp) {
		
		int listCount = mapper.getReportCount();
		
		Pagination pagination = new Pagination(cp, listCount);
		
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Report> reportList = mapper.selectReportList(rowBounds);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("reportList", reportList);
		
		return map;
	}

	@Override
	public int manageReport(Report updateReport) {
		return mapper.manageReport(updateReport);
	}

	@Override
	public Report selectTarget(int reportNo) {
		return mapper.selectTarget(reportNo);
	}
	
}
