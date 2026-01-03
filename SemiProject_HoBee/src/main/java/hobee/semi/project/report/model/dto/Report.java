package hobee.semi.project.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {

	private int reportNo;
	private String reportReason;
	private String reportDetail;
	private String reportDate;
	private String reportStatus;
	private String targetType;
	private int targetNo;
	private int reporterMemberNo;
	private int reportedMemberNo;
	
	private String reporterNickname;
	private String reportedNickname;
	
	
	
}
