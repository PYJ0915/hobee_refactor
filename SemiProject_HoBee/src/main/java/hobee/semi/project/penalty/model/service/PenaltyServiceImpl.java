package hobee.semi.project.penalty.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Pagination;
import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.mapper.PenaltyMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PenaltyServiceImpl implements PenaltyService{

	private final PenaltyMapper mapper;
	
	@Override
	public Penalty selectPenalty(int memberNo) {
		return mapper.selectPenalty(memberNo);
	}

	@Override
	public void expirePenalty(int penaltyNo) {
		mapper.expirePenalty(penaltyNo);
	}

	@Override
	public Map<String, Object> selectPenaltyList(int cp) {
		
		int listCount = mapper.getPenaltyCount();

		Pagination pagination = new Pagination(cp, listCount);

		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<Penalty> penaltyList = mapper.selectPenaltyList(rowBounds);

		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("penaltyList", penaltyList);

		return map;
	}

	@Override
	public int managePenalty(int penaltyNo) {
		return mapper.managePenalty(penaltyNo);
	}

}
