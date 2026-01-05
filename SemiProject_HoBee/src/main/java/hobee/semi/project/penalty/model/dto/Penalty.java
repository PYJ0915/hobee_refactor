package hobee.semi.project.penalty.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Penalty {
	
	private int penaltyNo;
	private String penaltyType;
	private LocalDateTime penaltyStartDate;
	private LocalDateTime penaltyEndDate;
	private String penaltyReason;
	private String penaltyStatus;
	private int memberNo;
	
	private String memberNickname;
}
