package hobee.semi.project.penalty.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.mapper.PenaltyMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class PenaltyServiceImpl implements PenaltyService{

	@Autowired
	private PenaltyMapper mapper;
	
	@Override
	public Penalty selectPenalty(int memberNo) {
		return mapper.selectPenalty(memberNo);
	}

	@Override
	public void expirePenalty(int penaltyNo) {
		mapper.expirePenalty(penaltyNo);
	}

}
