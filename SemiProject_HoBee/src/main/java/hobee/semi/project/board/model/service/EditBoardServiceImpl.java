package hobee.semi.project.board.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Board;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class EditBoardServiceImpl implements EditBoardService{
	
	
	
	
	
	
	@Override
	public int boardInsert(Board inputBoard) {

		
		
		return 0;
	}

}
