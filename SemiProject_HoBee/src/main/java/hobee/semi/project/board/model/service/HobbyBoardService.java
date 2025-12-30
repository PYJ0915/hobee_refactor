package hobee.semi.project.board.model.service;

import java.util.List;
import java.util.Map;

import hobee.semi.project.board.model.dto.Board;

public interface HobbyBoardService {

	Map<String, Object> selectBoardList(int boardCode, int cp);

	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

	List<Board> hobbyBestList(int boardCode);

	List<Board> noticeList(int noticeBoardCode);



}
