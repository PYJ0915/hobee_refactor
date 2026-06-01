package hobee.semi.project.challenge.model.service;

import java.util.List;
import java.util.Map;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.challenge.model.dto.Cert;
import hobee.semi.project.challenge.model.dto.Challenge;

public interface ChallengeService {

	void createChallenge(Challenge challenge);

	List<Challenge> getChallengeList(String status, String type);

	Challenge getChallenge(int challengeNo, int memberNo);

	Map<String, Object> joinChallenge(int challengeNo, int memberNo);

	Map<String, Object> certify(int challengeNo, int memberNo, Cert cert);

	List<Challenge> getMyChallenges(int memberNo);
	
	List<Cert> getCertList(int challengeNo, int memberNo);

	void closeExpiredChallenges(); // 스케줄러용
}
