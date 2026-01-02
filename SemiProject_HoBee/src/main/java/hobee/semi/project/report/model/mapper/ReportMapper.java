package hobee.semi.project.report.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.report.model.dto.Report;

@Mapper
public interface ReportMapper {

	int insertReport(Report insertReport);

	int getReportCount();
	
	List<Report> selectReportList(RowBounds rowBounds);

	int manageReport(Report updateReport);

}
