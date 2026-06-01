package hobee.semi.project.gathering.model.dto;

import java.util.List;

import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gathering {

    private int    gatheringNo;
    private int    boardNo;
    private String gatheringDate;   // 화면 표시용 문자열
    private String gatheringPlace;
    private String gatheringPlaceDetail;
    private double placeLat;
    private double placeLng;
    private int    maxMember;
    private int    currentMember;
    private String gatheringStatus; // OPEN / CLOSED / DONE
    private int    roomNo;

    // 표시용
    private List<MemberDTO> memberList; // 참여자 목록
    private boolean isJoined;           // 내가 참여했는지
    private String myJoinStatus;
    
    private List<MemberDTO> pendingList;  // 신청 대기
    private List<MemberDTO> approvedList; // 확정 참여자
}