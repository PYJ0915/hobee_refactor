package hobee.semi.project.penalty.model.service;

import java.util.Map;

import hobee.semi.project.penalty.model.dto.Penalty;

public interface PenaltyService {

	Penalty selectPenalty(int memberNo);

	void expirePenalty(int penaltyNo);

	Map<String, Object> selectPenaltyList(int cp);

	int managePenalty(int penaltyNo);

}
