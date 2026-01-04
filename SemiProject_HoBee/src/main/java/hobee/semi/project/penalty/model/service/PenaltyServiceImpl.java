package hobee.semi.project.penalty.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.penalty.model.dto.Penalty;

@Service
@Transactional(rollbackFor = Exception.class)
public class PenaltyServiceImpl implements PenaltyService{

	@Override
	public Penalty selectPenalty(int memberNo) {
		// TODO Auto-generated method stub
		return null;
	}

}
