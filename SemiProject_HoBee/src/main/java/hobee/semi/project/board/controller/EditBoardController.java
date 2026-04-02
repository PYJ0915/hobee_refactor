package hobee.semi.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.EditBoardService;
import hobee.semi.project.board.model.service.FreeBoardService;
import hobee.semi.project.board.model.service.HobbyBoardService;
import hobee.semi.project.board.model.service.BoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("editBoard")
@RequiredArgsConstructor
public class EditBoardController {


	private final EditBoardService service;
	private final FreeBoardService freeService;
	private final HobbyBoardService hobbyService;
	private final BoardService notiService;
	
	
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
	
	@PostMapping("/imageUpload")
    @ResponseBody
    public String imageUpload(@RequestParam("file") MultipartFile file) throws Exception {
        return service.imageUpload(file);
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
		
		int boardCode = 0;
	    switch(boardName) {
	        case "notice" :  boardCode = 1; break; 
	        case "hobby" :   boardCode = 2; break; 
	        case "free" :    boardCode = 3; break;
	    }
		
		// 1. 로그인한 회원 번호를 세팅
	    inputBoard.setMemberNo(loginMember.getMemberNo());
	    // 2. 게시판 이름 세팅
	    inputBoard.setBoardCode(boardCode);

	    // 3. 서비스 호출 (비즈니스 로직에서 이미지 DB 매칭 처리)
	    int boardNo = service.boardInsert(inputBoard);

	    String message = null;
	    String path = null;

	    if (boardNo > 0) {
	        message = "게시글이 성공적으로 등록되었습니다.";
	        path = "redirect:/" + boardName + "/" + boardNo; // 상세조회 페이지로
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

		int boardCode = 0;
	    switch(boardName) {
	        case "notice":  boardCode = 1; break; 
	        case "hobby":   boardCode = 2; break; 
	        case "free" :   boardCode = 3; break;
	    }
		
		// 1. 로그인한 회원 번호를 세팅
	    inputBoard.setMemberNo(loginMember.getMemberNo());
	    // 2. 게시판 이름 세팅
	    inputBoard.setBoardCode(boardCode);

	    inputBoard.setCategoryCode(categoryCode);


	    // 3. 서비스 호출 (비즈니스 로직에서 이미지 DB 매칭 처리)
	    int boardNo = service.boardInsert(inputBoard);
	    
	    String message = null;
	    String path = null;
	    
	    
	    if (boardNo > 0) {
	        // 등록 성공 시
	        message = "게시글이 성공적으로 등록되었습니다.";
	        // 상세조회 페이지 경로 (예: /hobby/1/123)
	        path = "redirect:/" + boardName + "/" + categoryCode + "/" + boardNo;
	    } else {
	        // 등록 실패 시
	        message = "게시글 등록에 실패했습니다. 다시 시도해 주세요.";
	        // 다시 글쓰기 화면으로 (상대 경로 insert)
	        path = "redirect:insert";
	    }

	    ra.addFlashAttribute("message", message);
	    return path;
	}
	
	/** 공지게시판 / 자유게시판 삭제 메서드
	 * @param boardName
	 * @param boardNo
	 * @param cp
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@RequestMapping(value="/{boardName:[a-zA-Z]+}/{boardNo:[0-9]+}/delete",
			method= {RequestMethod.POST})
public String BoardDelete(@PathVariable("boardName") String boardName,
						@PathVariable("boardNo") int boardNo,
						@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
						@SessionAttribute("loginMember") MemberDTO loginMember,
						RedirectAttributes ra ) { 
		
		Map<String, Object> map = new HashMap<>();
		map.put("boardName", boardName);
		map.put("memberNo", loginMember.getMemberNo());
		map.put("boardNo", boardNo);
		map.put("authorLevel", loginMember.getAuthorLevel());
		map.put("categoryCode", 0);
		
		int result = service.boardDelete(map);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = String.format("/%s?cp=%d", boardName, cp);
							   
			message = "글 삭제가 완료되었습니다";
			
		} else {
			path = String.format("/%s/%d/?cp=%d", boardName, boardNo, cp);
			
			message = "삭제 실패됨 ....";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
		
	
	}
	
	
	/** 취미게시판 삭제 메서드
	 * @param boardName
	 * @param categoryCode
	 * @param boardNo
	 * @param cp
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@RequestMapping(value="/{boardName:[a-zA-Z]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/delete",
			method= {RequestMethod.POST})
public String hobbyBoardDelete(@PathVariable("boardName") String boardName,
						@PathVariable("categoryCode") int categoryCode,
						@PathVariable("boardNo") int boardNo,
						@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
						@SessionAttribute("loginMember") MemberDTO loginMember,
						RedirectAttributes ra ) { 
		
		Map<String, Object> map = new HashMap<>();
		map.put("boardName", boardName);
		map.put("categoryCode", categoryCode);
		map.put("memberNo", loginMember.getMemberNo());
		map.put("boardNo", boardNo);
		map.put("authorLevel", loginMember.getAuthorLevel());
		
		int result = service.boardDelete(map);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = String.format("/hobby/%d?cp=%d", categoryCode, cp);
							   // /hobby/1/?cp=1
			message = "글 삭제가 완료되었습니다";
			
		} else {
			path = String.format("/hobby/%d/%d?cp=%d", categoryCode, boardNo, cp);
							   // /hobby/1/24?cp=1
			message = "삭제 실패됨 ....";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
		
	}
	
	/** 게시글 수정 화면 이동
	 * @param boardName
	 * @param boardNo
	 * @param categoryCode
	 * @param inputBoard
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = {
		    "/{boardName:[a-zA-Z]+}/{boardNo:[0-9]+}/update",
		    "/{boardName:[a-zA-Z]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/update"
		})
		public String boardUpdate(
		        @PathVariable("boardName") String boardName,
		        @PathVariable("boardNo") int boardNo,
		        @PathVariable(value="categoryCode", required=false) Integer categoryCode, // Integer는 null을 포함하기 때문에 사용해야함
		        @SessionAttribute("loginMember") MemberDTO loginMember,
		        Model model,
		        RedirectAttributes ra) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put("boardNo", boardNo);
		
		
		Board board = null;

	    switch(boardName) {
	        case "notice" : board = notiService.selectBoardDetail(map); break;
	        case "free"   : board = freeService.selectBoardDetail(map); break;
	        case "hobby"  : board = hobbyService.selectBoardDetail(map); break;
	    }

	    
	    model.addAttribute("board", board);
	    model.addAttribute("boardName", boardName); 
	    
	    return "board/editBoard";
	}
	
	
	
	/** 게시글 수정 적용
	 * @param boardName
	 * @param boardNo
	 * @param categoryCode
	 * @param inputBoard
	 * @param cp
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping(value = {
		    "/{boardName:[a-zA-Z]+}/{boardNo:[0-9]+}/update",
		    "/{boardName:[a-zA-Z]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/update"
		})
	public String boardUpdate(
			@PathVariable("boardName") String boardName,
			@PathVariable("boardNo") int boardNo,
			@PathVariable(value = "categoryCode", required = false) Integer categoryCode,
			Board inputBoard,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") MemberDTO loginMember,
			RedirectAttributes ra
			) {
		
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		inputBoard.setAuthorLevel(loginMember.getAuthorLevel());
		if(categoryCode != null) {
			inputBoard.setCategoryCode(categoryCode);
		}
		
		int result = service.boardUpdate(inputBoard);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "게시글이 수정 되었습니다";
			
				    switch(boardName) {
			        case "notice" : path = String.format("/notice/%d?cp=%d", boardNo, cp); break;
			        case "free"   : path = String.format("/free/%d?cp=%d", boardNo, cp); break;
			        case "hobby"  : path = String.format("/hobby/%d/%d?cp=%d", categoryCode, boardNo, cp); break;
			    }
				    
				
			
		} else {
			message = "수정 실패";
			path = "update";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	
	
	
	
	
	
	
	
	
}
