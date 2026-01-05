package hobee.semi.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.board.model.dto.Board;

@Mapper
public interface NoticeBoardMapper {

	int getListCount(int boardCode);

	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	int getSearchCount(Map<String, Object> paramMap);

	List<Board> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	Board selectBoardDetail(Map<String, Object> map);

	int deleteBoardLike(Map<String, Integer> map);

	int insertBoardLike(Map<String, Integer> map);

	int selectLikeCount(Integer boardNo);

	int updateViewCount(int boardNo);

	int selectViewCount(int boardNo);

}
