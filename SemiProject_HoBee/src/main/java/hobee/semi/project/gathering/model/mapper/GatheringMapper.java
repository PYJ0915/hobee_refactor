package hobee.semi.project.gathering.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import hobee.semi.project.gathering.model.dto.Gathering;
import hobee.semi.project.member.model.dto.MemberDTO;

@Mapper
public interface GatheringMapper {

	// 모임 생성 (게시글 작성과 동시에)
	void insertGathering(Gathering gathering);

	// 게시글 번호로 모임 조회
	Gathering selectGathering(int boardNo);

	// 참여 신청
	int joinGathering(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);

	// 신청 수락
	int approveJoin(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);

	// 신청 거절
	int rejectJoin(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);

	// 참여 취소
	int cancelGathering(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);

	// 신청자 목록 (PENDING)
	List<MemberDTO> selectPendingMembers(int gatheringNo);

	// 확정된 참여자 목록 (APPROVED)
	List<MemberDTO> selectApprovedMembers(int gatheringNo);

	// 참여 여부 확인
	int checkJoined(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);

	// 현재 인원 +1
	int incrementMember(int gatheringNo);

	// 현재 인원 -1
	int decrementMember(int gatheringNo);

	// 참여자 목록
	List<MemberDTO> selectGatheringMembers(int gatheringNo);

	// 상태 변경 (OPEN → CLOSED)
	int updateStatus(@Param("gatheringNo") int gatheringNo, @Param("gatheringStatus") String gatheringStatus);

	// 채팅방 번호 연결
	int updateRoomNo(@Param("gatheringNo") int gatheringNo, @Param("roomNo") int roomNo);

	void joinApproved(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);
	
	int checkApproved(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);
	
	int selectBoardNoByGatheringNo(int gatheringNo);
	
	String selectMyJoinStatus(@Param("gatheringNo") int gatheringNo, @Param("memberNo") int memberNo);
	
	int updateGathering(Gathering gathering);
	
	Gathering selectGatheringByNo(int gatheringNo);

	String selectBoardTitle(int gatheringNo);
}
