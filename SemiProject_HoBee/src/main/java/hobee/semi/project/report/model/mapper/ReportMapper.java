package hobee.semi.project.report.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.report.model.dto.Report;

@Mapper
public interface ReportMapper {

	int insertReport(Report insertReport);

}
