package hobee.semi.project.board.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.EditBoardService;
import hobee.semi.project.board.model.service.FreeBoardService;
import hobee.semi.project.board.model.service.HobbyBoardService;
import hobee.semi.project.board.model.service.NoticeBoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;

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
	
	
	
	/** 자유게시판 / 공지게시판 글작성 기능
	 * @return
	 */
	@PostMapping("/{boardName:[a-zA-Z]+}/insert")
	public String boardInsert(@PathVariable("boardName") String boardName,
			@ModelAttribute Board inputBoard,
			@SessionAttribute("loginMember") MemberDTO loginMember,
			RedirectAttributes ra
			) throws IllegalStateException, IOException{
		
		
		
		// 1. 로그인한 회원 번호를 세팅
	    inputBoard.setMemberNo(loginMember.getMemberNo());
	    // 2. 게시판 이름 세팅
	    inputBoard.setBoardName(boardName);

	    // 3. 서비스 호출 (비즈니스 로직에서 이미지 DB 매칭 처리)
	    int boardNo = service.boardInsert(inputBoard);

	    String message = null;
	    String path = null;

	    if (boardNo > 0) {
	        message = "게시글이 성공적으로 등록되었습니다.";
	        path = "redirect:/board/" + boardName + "/" + boardNo; // 상세조회 페이지로
	    } else {
	        message = "게시글 등록에 실패했습니다. 다시 시도해 주세요.";
	        path = "redirect:insert";
	    }

	    ra.addFlashAttribute("message", message);
	    return path;
		
	}
	
	/** 취미게시판 글작성 기능
	 * @param boardName
	 * @param categoryCode
	 * @param inputBoard
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@PostMapping("/{boardName:[a-zA-Z]+}/{categoryCode:[0-9]+}/insert")
	public String boardInsertHobby(
	        @PathVariable("boardName") String boardName,
	        @PathVariable("categoryCode") int categoryCode,
	        @ModelAttribute Board inputBoard,
	        @SessionAttribute("loginMember") MemberDTO loginMember,
	        RedirectAttributes ra) throws IllegalStateException, IOException {

	    inputBoard.setMemberNo(loginMember.getMemberNo());
	    inputBoard.setBoardName(boardName);
	    inputBoard.setCategoryCode(categoryCode);

	    int boardNo = service.boardInsert(inputBoard);

	    
	    String message = null;
	    String path = null;
	    
	    
	    if (boardNo > 0) {
	        // 등록 성공 시
	        message = "게시글이 성공적으로 등록되었습니다.";
	        // 상세조회 페이지 경로 (예: /board/hobby/1/123)
	        path = "redirect:/board/" + boardName + "/" + categoryCode + "/" + boardNo;
	    } else {
	        // 등록 실패 시
	        message = "게시글 등록에 실패했습니다. 다시 시도해 주세요.";
	        // 다시 글쓰기 화면으로 (상대 경로 insert)
	        path = "redirect:insert";
	    }

	    ra.addFlashAttribute("message", message);
	    return path;
	}
	
	
	
	
	
	
	
	
}
