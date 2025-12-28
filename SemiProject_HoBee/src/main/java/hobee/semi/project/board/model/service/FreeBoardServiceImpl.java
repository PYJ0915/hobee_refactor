package hobee.semi.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.dto.Pagination;
import hobee.semi.project.board.model.mapper.FreeBoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class FreeBoardServiceImpl implements FreeBoardService{
	
	private final FreeBoardMapper mapper;

	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {
		
		
		// 1. 해당 게시판의 전체 게시글 수 조회 (삭제 안 된 것)
        int listCount = mapper.getListCount(boardCode);

        // 2. Pagination 객체 생성 (현재 페이지, 전체 게시글 수 전달)
        Pagination pagination = new Pagination(cp, listCount);

        // 3. 특정 페이지 목록 조회
        // RowBounds(건너뛸 개수, 가져올 개수)
        int limit = pagination.getLimit();
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);

        // 4. 결과 담기
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("boardList", boardList);

        return map;
	}

	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {
		
		
		// 1. 검색 조건에 맞는 게시글 수 조회
        int listCount = mapper.getSearchCount(paramMap);

        // 2. Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount);

        // 3. 검색 목록 조회 (RowBounds 사용)
        int limit = pagination.getLimit();
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);

        // 4. 결과 담기
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("boardList", boardList);

        return map;
	}

	@Override
	public List<Board> freeBestList(int boardCode) {
	    // 상위 5개만 가져오기 위해 RowBounds 사용 (offset: 0, limit: 5)
	    RowBounds rowBounds = new RowBounds(0, 5);
	    
	    // Mapper 호출 시 boardCode와 rowBounds 전달
	    return mapper.selectFreeBestList(boardCode, rowBounds);
	}

	@Override
	public List<Board> noticeList(int noticeBoardCode) {
	    // 최신 공지사항 5개만 가져오기 (offset: 0, limit: 5)
	    RowBounds rowBounds = new RowBounds(0, 5);
	    
	    return mapper.selectNoticeList(noticeBoardCode, rowBounds);
	}
	
	

	

	
}
