package hobee.semi.project.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import hobee.semi.project.board.model.service.EditBoardService;
import hobee.semi.project.board.model.service.FreeBoardService;
import hobee.semi.project.board.model.service.HobbyBoardService;
import hobee.semi.project.board.model.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@RequiredArgsConstructor
public class EditBoardController {

	private final EditBoardService service;
	private final FreeBoardService freeService;
	private final HobbyBoardService hobbyService;
	private final NoticeBoardService notiService;
	
	
	/** 자유게시판 및 공지게시판 글작성 화면 조회
	 * @param boardName
	 * @return
	 */
	@GetMapping("/{boardName:[a-zA-Z]+}/insert")
	public String boardInsert(@PathVariable("boardName") String boardName) {
		
		
		return "board/editBoard";
	}
	
	
	
	/** 취미게시판 글작성 화면 조회
	 * @param boardName
	 * @param categoryCode
	 * @return
	 */
	@GetMapping("/{boardName:[a-zA-Z]+}/{categoryCode:[0-9]+}/insert")
    public String boardInsertHobby(
            @PathVariable("boardName") String boardName,
            @PathVariable("categoryCode") int categoryCode) {
        
        return "board/editBoard";
    }
	
	
	
	
	
	
}
