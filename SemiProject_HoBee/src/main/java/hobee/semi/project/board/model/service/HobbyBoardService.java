package hobee.semi.project.board.model.service;

import java.util.List;
import java.util.Map;

import hobee.semi.project.board.model.dto.Board;

public interface HobbyBoardService {

	Map<String, Object> selectBoardList(int categoryCode, int cp);

	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

	List<Board> hobbyBestList(int categoryCode);

	List<Board> noticeList(int noticeBoardCode);

	String selectCategoryName(int categoryCode);

	Board selectBoardDetail(Map<String, Object> map);

	int boardLike(Map<String, Integer> map);

	int updateViewCount(int boardNo);



}
