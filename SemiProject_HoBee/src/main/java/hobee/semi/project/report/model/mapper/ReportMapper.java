package hobee.semi.project.report.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.report.model.dto.Report;

@Mapper
public interface ReportMapper {

	int insertReport(Report insertReport);

	List<Report> selectReportList();

}
