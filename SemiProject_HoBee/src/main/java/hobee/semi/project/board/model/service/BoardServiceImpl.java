package hobee.semi.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.dto.Pagination;
import hobee.semi.project.board.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

	private final BoardMapper mapper;

	@Override
	public Map<String, Object> selectBoardList(int boardCode, Integer categoryCode, int cp, String sort, String dir) {

		// 1. 해당 게시판의 전체 게시글 수 조회 (삭제 안 된 것)
		int listCount = mapper.getListCount(boardCode, categoryCode);

		// 2. Pagination 객체 생성 (현재 페이지, 전체 게시글 수 전달)
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 페이지 목록 조회
		RowBounds rowBounds = createRowBounds(cp, listCount);

		List<Board> boardList = mapper.selectBoardList(boardCode, categoryCode, sort, dir, rowBounds);

		// 4. 결과 담기
		return toBoardListMap(pagination, boardList);
	}

	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {

		// 1. 검색 조건에 맞는 게시글 수 조회
		int listCount = mapper.getSearchCount(paramMap);

		// 2. Pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 검색 목록 조회 (RowBounds 사용)
		RowBounds rowBounds = createRowBounds(cp, listCount);

		List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);

		// 4. 결과 담기
		return toBoardListMap(pagination, boardList);
	}

	@Override
	public Board selectBoardDetail(Map<String, Object> map) {
		return mapper.selectBoardDetail(map);
	}

	@Override
	public int boardLike(Map<String, Integer> map) {

		int result = 0;

		if (map.get("likeCheck") == 1) {
			result = mapper.deleteBoardLike(map);

		} else {
			result = mapper.insertBoardLike(map);
		}

		// 좋아요를 삭제하거나 삽입 성공했다면 좋아요 개수 조회해서 반환
		if (result > 0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}

		return -1;
	}

	@Override
	public int updateViewCount(int boardNo) {

		// 1. 조회 수 1 증가 (UPDATE)
		int result = mapper.updateViewCount(boardNo);

		if (result > 0) {
			return mapper.selectViewCount(boardNo);
		}

		return -1;
	}

	@Override
	public Map<String, Object> selectMyBoardList(Map<String, Object> queryMap, int cp) {

		// 1. 내가 작성한 게시글 수 조회 (MEMBER_NO 조건 포함)
		int listCount = mapper.getMyListCount(queryMap);

		// 2. Pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 페이지의 내 게시글 목록 조회
		RowBounds rowBounds = createRowBounds(cp, listCount);

		// Mapper에 selectMyBoardList(queryMap, rowBounds) 추가
		List<Board> boardList = mapper.selectMyBoardList(queryMap, rowBounds);

		// 4. 결과 담기
		return toBoardListMap(pagination, boardList);
	}

	@Override
	public List<Board> selectBestList(int boardCode, Integer categoryCode) {
		// 상위 5개만 가져오기 위해 RowBounds 사용 (offset: 0, limit: 5)
		RowBounds rowBounds = new RowBounds(0, 5);

		// Mapper 호출 시 boardCode와 rowBounds 전달
		return mapper.selectBestList(boardCode, categoryCode, rowBounds);
	}

	@Override
	public List<Board> noticeList(int noticeBoardCode) {
		// 최신 공지사항 5개만 가져오기 (offset: 0, limit: 5)
		RowBounds rowBounds = new RowBounds(0, 5);

		return mapper.selectNoticeList(noticeBoardCode, rowBounds);
	}


	/** rowBounds 만드는 함수 (공통 작업)
	 * RowBounds(건너뛸 개수, 가져올 개수)
	 */
	private RowBounds createRowBounds(int cp, int listCount) {
		Pagination pagination = new Pagination(cp, listCount);
		int offset = (cp - 1) * pagination.getLimit();
		return new RowBounds(offset, pagination.getLimit());
	}

	/** 반환할 map 만들어서 반환 (공통 작업)
	 *
	 */
	private Map<String, Object> toBoardListMap(Pagination pagination, List<Board> boardList) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("pagination", pagination);
		resultMap.put("boardList", boardList);
		return resultMap;
	}

}
