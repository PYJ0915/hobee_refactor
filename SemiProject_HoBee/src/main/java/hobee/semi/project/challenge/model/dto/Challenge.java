package hobee.semi.project.challenge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

	private int    challengeNo;
    private int    memberNo;
    private String challengeTitle;
    private String challengeDesc;
    private String challengeType;
    private int    goalCount;
    private String startDate;
    private String endDate;
    private String challengeStatus;   // OPEN / CLOSED / DONE
    private String createDate;
    private String memberNickname;    // 표시용

    // 집계용
    private int    participantCount;  // 참여자 수
    private int    myCount;           // 내 현재 인증 횟수
    private boolean isJoined;         // 내가 참여 중인지
    private boolean isComplete;       // 내가 목표 달성했는지
	
}
