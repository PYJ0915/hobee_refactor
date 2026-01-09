package hobee.semi.project.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import hobee.semi.project.board.model.dto.Board;


public interface EditBoardService {

	int boardInsert(Board inputBoard);

	String imageUpload(MultipartFile file) throws Exception;

	int boardDelete(Map<String, Object> map);

	int boardUpdate(Board inputBoard);

	List<String> selectDbImgList();

}
