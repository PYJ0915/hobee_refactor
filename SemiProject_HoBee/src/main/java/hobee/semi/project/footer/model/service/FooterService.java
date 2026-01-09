package hobee.semi.project.footer.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Pagination;
import hobee.semi.project.footer.model.dto.CS;
import hobee.semi.project.footer.model.mapper.FooterMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class FooterService {

	@Autowired
	private FooterMapper mapper;
	
	public int insertCS(CS cs) {
		return mapper.insertCS(cs);
	}

	public Map<String, Object> selectCSList(int cp) {
		int listCount = mapper.getCSCount();

		Pagination pagination = new Pagination(cp, listCount);

		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		List<CS> csList = mapper.selectCSList(rowBounds);

		Map<String, Object> map = new HashMap<>();

		map.put("pagination", pagination);
		map.put("csList", csList);

		return map;
	}

	public CS selectTarget(int csNo) {
		return mapper.selectTarget(csNo);
	}

	public int csComplete(int csNo) {
		return mapper.csComplete(csNo);
	}
	
}
