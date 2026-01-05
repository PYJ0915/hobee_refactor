package hobee.semi.project.report.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.report.model.dto.Report;

@Mapper
public interface ReportMapper {

	int insertReport(Report insertReport);

	int getReportCount();
	
	List<Report> selectReportList(RowBounds rowBounds);

	int manageReport(Report updateReport);

	Report selectTarget(int reportNo);

	int selectReportedMemberNo(int reportNo);

	int selectReportCount(int memberNo);

	int selectSuspendCount(int memberNo);

	int selectWarningCount(int memberNo);

	String selectPenaltyReasaon(int memberNo);

	int insertPenalty(Map<String, Object> map);

	int selectActivePermanentCount(int memberNo);

}
