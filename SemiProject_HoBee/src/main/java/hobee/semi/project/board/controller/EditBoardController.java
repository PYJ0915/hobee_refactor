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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.BoardService;
import hobee.semi.project.board.model.service.EditBoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@RequiredArgsConstructor
public class EditBoardController {

	private final EditBoardService service;

	private final BoardService boardService;

	/**
	 * 글작성 화면 조회
	 * 
	 * @param boardName
	 * @return
	 */
	@GetMapping({ "/{boardCode:[0-9]+}/insert", "/{boardCode:[0-9]+}/{categoryCode:[0-9]+}/insert" })
	public String boardInsert(@PathVariable("boardCode") int boardCode,
			@PathVariable(name = "categoryCode", required = false) Integer categoryCode) {
		return "board/editBoard";
	}

	@PostMapping("/imageUpload")
	@ResponseBody
	public String imageUpload(@RequestParam("file") MultipartFile file) throws Exception {
		return service.imageUpload(file);
	}

	/**
	 * 게시판 글 작성 기능
	 * 
	 * @return
	 */
	@PostMapping({ "/{boardCode:[0-9]+}/insert", "/{boardCode:[0-9]+}/{categoryCode:[0-9]+}/insert" })
	public String boardInsert(@PathVariable("boardCode") int boardCode,
			@PathVariable(name = "categoryCode", required = false) Integer categoryCode,
			@ModelAttribute Board inputBoard,
			@SessionAttribute("loginMember") MemberDTO loginMember, RedirectAttributes ra)
			throws IllegalStateException, IOException {

		// 1. 로그인한 회원 번호를 세팅
		inputBoard.setMemberNo(loginMember.getMemberNo());

		// 2. 게시판 이름 세팅
		inputBoard.setBoardCode(boardCode);
		
		// 취미 게시판인 경우 카테고리 코드 세팅
		if (categoryCode != null) inputBoard.setCategoryCode(categoryCode);

		// 3. 서비스 호출 (비즈니스 로직에서 이미지 DB 매칭 처리)
		int boardNo = service.boardInsert(inputBoard);
		
		String message = null;
		String path = null;

		if (boardNo > 0) {
			message = "게시글이 성공적으로 등록되었습니다.";
			
			if (categoryCode != null) {
				path = String.format("redirect:/board/detail/%d/%d/%d", boardCode, categoryCode, boardNo);
			} else {
				path = String.format("redirect:/board/detail/%d/%d", boardCode, boardNo);
			}
			
		} else {
			message = "게시글 등록에 실패했습니다. 다시 시도해 주세요.";
			path = "redirect:insert";
		}

		ra.addFlashAttribute("message", message);
		return path;

	}


	/**
	 * 게시판 삭제 기능
	 * 
	 * @param boardName
	 * @param boardNo
	 * @param cp
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping({"/{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete", "/{boardCode:[0-9]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/delete"})
	public String BoardDelete(@PathVariable("boardCode") int boardCode,
			@PathVariable(name = "categoryCode", required = false) Integer categoryCode,
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") MemberDTO loginMember, RedirectAttributes ra) {

		Map<String, Object> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("categoryCode", categoryCode != null ? categoryCode : 0);
		map.put("memberNo", loginMember.getMemberNo());
		map.put("boardNo", boardNo);
		map.put("authorLevel", loginMember.getAuthorLevel());

		int result = service.boardDelete(map);

		String path = null;
		String message = null;

		if (result > 0) {
			message = "글 삭제가 완료되었습니다";
			
			if(categoryCode != null) {
				path = String.format("/board/list/%d/%d?cp=%d", boardCode, categoryCode, cp);
			} else {
				path = String.format("/board/list/%d?cp=%d", boardCode, cp);
			}
			
		} else {
			message = "게시글 삭제에 실패했습니다. 다시 시도해 주세요.";
			
			if(categoryCode != null) {
				path = String.format("/board/detail/%d/%d/%d?cp=%d", boardCode, categoryCode, boardNo, cp);
			} else {
				path = String.format("/board/detail/%d/%d/?cp=%d", boardCode, boardNo, cp);
			}
			
		}
		
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}


	/**
	 * 게시글 수정 화면 조회
	 * 
	 * @param boardName
	 * @param boardNo
	 * @param categoryCode
	 * @param inputBoard
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@GetMapping({ "/{boardCode:[0-9]+}/{boardNo:[0-9]+}/update",
			"/{boardCode:[0-9]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/update" })
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@PathVariable(value = "categoryCode", required = false) Integer categoryCode, // Integer는 null을 포함하기 때문에
																							// 사용해야함
			@SessionAttribute("loginMember") MemberDTO loginMember, Model model, RedirectAttributes ra)
			throws Exception {

		Map<String, Object> map = new HashMap<>();
		map.put("boardNo", boardNo);

		Board board = null;

		board = boardService.selectBoardDetail(map);

		model.addAttribute("board", board);
		model.addAttribute("boardCode", boardCode);

		return "board/editBoard";
	}

	/**
	 * 게시글 수정 기능
	 * 
	 * @param boardName
	 * @param boardNo
	 * @param categoryCode
	 * @param inputBoard
	 * @param cp
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping({ "/{boardCode:[0-9]+}/{boardNo:[0-9]+}/update",
			"/{boardCode:[0-9]+}/{categoryCode:[0-9]+}/{boardNo:[0-9]+}/update" })
	public String boardUpdate(@PathVariable("boardCode") int boardCode, @PathVariable("boardNo") int boardNo,
			@PathVariable(value = "categoryCode", required = false) Integer categoryCode, Board inputBoard,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") MemberDTO loginMember, RedirectAttributes ra) {

		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		inputBoard.setAuthorLevel(loginMember.getAuthorLevel());
		if (categoryCode != null) {
			inputBoard.setCategoryCode(categoryCode);
		}

		int result = service.boardUpdate(inputBoard);

		String message = null;
		String path = null;

		if (result > 0) {
			message = "게시글이 수정 되었습니다";

			if(categoryCode != null) {
				path = String.format("/board/detail/%d/%d/%d?cp=%d", boardCode, categoryCode, boardNo, cp);
			} else {
				path = String.format("/board/detail/%d/%d?cp=%d", boardCode, boardNo, cp);
			}
			
		} else {
			message = "수정 실패";
			path = "update";

		}
		
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}

}
