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
public class ReportServiceImpl implements ReportService {

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
		
		int result = mapper.manageReport(updateReport);
		
		if(result == 0) {
			return 0;
		}
		
		if(updateReport.getReportStatus().equals("REJECTED")) {
			return result;
		}
		
		// 승인을 누른 경우
		int memberNo = mapper.selectReportedMemberNo(updateReport.getReportNo());
		
		// 이미 영구 정지 시 penalty 인서트 방지
		int permanentCount = mapper.selectActivePermanentCount(memberNo);
		
			if (permanentCount > 0) {
				return result;
			}
			
		// 승인된 신고 개수 조회
		int count = mapper.selectReportCount(memberNo);
		// 경고 이력 조회
		int warningCount = mapper.selectWarningCount(memberNo);
		// 정지 이력 조회
		int suspendCount = mapper.selectSuspendCount(memberNo);
		
		// 가장 많은 신고 이유 조회해오기 => 제재 사유
		String penaltyReason =  mapper.selectPenaltyReasaon(memberNo);
		
		int plusDays = 0; // WARNING / PERMANENT는 endDate 사용 안 함
		String penaltyType = null;
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("penaltyReason", penaltyReason);
		
		
		if(count >= 5 && warningCount == 0) {
			// 경고
			penaltyType = "WARNING";
			
			map.put("penaltyType", penaltyType);
			map.put("plusDays", plusDays);
			
			// 제재 테이블에 인서트
			int resp = mapper.insertPenalty(map);
			
			if(resp == 0) {
				throw new RuntimeException();
			}
			
			return result;
		}
		
		if(count >= (suspendCount + 1) * 10) {
			// 정지
			
			penaltyType = "SUSPEND";
			
			switch (suspendCount + 1) {
				case 1:
					plusDays = 3;
					break;
				case 2:
					plusDays = 7;
					break;
				case 3:
					plusDays = 14;
					break;
				case 4:
					plusDays = 30;
					break;
				default:
					penaltyType = "PERMANENT";
					break;
			}
			
			map.put("penaltyType", penaltyType);
			map.put("plusDays", plusDays);
			
			int resp = mapper.insertPenalty(map);
			
			if(resp == 0) {
				throw new RuntimeException();
			}
			
			return result;
		}
		
		return result;
		
	}

	@Override
	public Report selectTarget(int reportNo) {
		return mapper.selectTarget(reportNo);
	}

}
