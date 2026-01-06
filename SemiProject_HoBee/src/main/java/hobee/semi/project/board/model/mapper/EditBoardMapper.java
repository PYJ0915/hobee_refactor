package hobee.semi.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.dto.BoardImg;

@Mapper
public interface EditBoardMapper {

	int insertImage(BoardImg img);

	int boardInsert(Board inputBoard);

	int updateImageBoardNo(Map<String, Object> map);

	int boardDelete(Map<String, Object> map);

	int boardUpdate(Board inputBoard);

	List<String> selectDbImageList();





}
