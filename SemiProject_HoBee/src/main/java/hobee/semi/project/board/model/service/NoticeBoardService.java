package hobee.semi.project.board.model.service;

import java.util.Map;

public interface NoticeBoardService {

	Map<String, Object> selectBoardList(int boardCode, int cp);

	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

}
