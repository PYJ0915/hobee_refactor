package hobee.semi.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	int getListCount(@Param("boardCode") int boardCode, @Param("categoryCode") Integer categoryCode);

	List<Board> selectBoardList(@Param("boardCode") int boardCode, @Param("categoryCode") Integer categoryCode, @Param("sort") String sort, @Param("dir") String dir, RowBounds rowBounds);

	int getSearchCount(Map<String, Object> paramMap);

	List<Board> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	Board selectBoardDetail(Map<String, Object> map);

	int deleteBoardLike(Map<String, Integer> map);

	int insertBoardLike(Map<String, Integer> map);

	int selectLikeCount(Integer boardNo);

	int updateViewCount(int boardNo);

	int selectViewCount(int boardNo);

	int getMyListCount(Map<String, Object> queryMap);

	List<Board> selectMyBoardList(Map<String, Object> queryMap, RowBounds rowBounds);

	List<Board> selectBestList(@Param("boardCode") int boardCode, @Param("categoryCode") Integer categoryCode, RowBounds rowBounds);

	List<Board> selectNoticeList(int noticeBoardCode, RowBounds rowBounds);
	
	int selectBoardMemberNo(int boardNo);

	int getBoardCode(int boardNo);

}
