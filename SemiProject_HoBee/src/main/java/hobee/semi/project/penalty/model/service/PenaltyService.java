package hobee.semi.project.penalty.model.service;

import hobee.semi.project.penalty.model.dto.Penalty;

public interface PenaltyService {

	Penalty selectPenalty(int memberNo);

	void expirePenalty(int penaltyNo);

}
