package hobee.semi.project.gathering.model.service;

import java.util.Map;

import hobee.semi.project.gathering.model.dto.Gathering;

public interface GatheringService {

	void createGathering(Gathering gathering, int memberNo);

	Gathering getGathering(int boardNo, int loginMemberNo);

	Map<String, Object> joinGathering(int gatheringNo, int memberNo);

	Map<String, Object> cancelGathering(int gatheringNo, int memberNo);

	int confirmGathering(int gatheringNo, int loginMemberNo);

	Map<String, Object> approveJoin(int gatheringNo, int targetMemberNo, int loginMemberNo);

	Map<String, Object> rejectJoin(int gatheringNo, int targetMemberNo, int loginMemberNo);
	
	int getBoardNo(int gatheringNo);
	
	void updateGathering(Gathering gathering);
}
