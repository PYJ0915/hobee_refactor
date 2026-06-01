package hobee.semi.project.challenge.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.challenge.model.dto.Challenge;

@Mapper
public interface ChallengeMapper {

	// 챌린지 생성
	void insertChallenge(Challenge challenge);

	// 챌린지 목록 조회
	List<Challenge> selectChallengeList(@Param("status") String status, @Param("type") String type);

	// 챌린지 상세 조회
	Challenge selectChallenge(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

	// 참여 신청
	void joinChallenge(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

	// 참여 여부 확인
	int checkJoined(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

	// 인증 횟수 +1
	int incrementCertCount(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

	// 목표 달성 처리
	int completeChallenge(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

	// 상태 변경 (OPEN → DONE)
	int updateStatus(@Param("challengeNo") int challengeNo, @Param("challengeStatus") String challengeStatus);

	// 만료된 챌린지 목록 (스케줄러용)
	List<Challenge> selectExpiredChallenges();

	// 내 챌린지 목록
	List<Challenge> selectMyChallenges(int memberNo);

	List<Board> selectCertList(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);
}