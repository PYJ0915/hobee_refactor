package hobee.semi.project.board.model.service;

import java.util.Map;

import hobee.semi.project.board.model.dto.Board;

public interface NoticeBoardService {

	Map<String, Object> selectBoardList(int boardCode, int cp);

	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

	Board selectBoardDetail(Map<String, Object> map);

	int boardLike(Map<String, Integer> map);

	int updateViewCount(int boardNo);

}
