package hobee.semi.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.NoticeBoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("notice")
@Slf4j
@RequiredArgsConstructor
public class NoticeBoardController {
	
	private final NoticeBoardService service;
	
	@GetMapping("")
	public String selectBoardList(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
				Model model,
				@RequestParam Map<String, Object> paramMap) {
		
		int boardCode = 1;
		
		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;
		
		// 검색이 아닌 경우 --> paramMap 은 {}
		if(paramMap.get("key") == null) {
			
			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(boardCode, cp);
	
		} else { // 검색인 경우  
			//--> paramMap에 key라는 k에 접근하면 매핑된 value 반환
			//--> ex) {key=w, query=짱구}
			//--> --> w 반환됨
			
			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			// -> paramMap은 {key=w, query=짱구, boardCode=1}
			
			// 검색(내가 검색하고 싶은 게시글 목록 조회) 서비스 호출
			map = service.searchList(paramMap, cp);
			
		}
		
		// model에 결과 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("boardCode", boardCode);
		
		// src/main/resources/templates/board/boardList.html 로 forward
		return "board/noticeBoard";
	}
	
	@GetMapping("{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardNo") int boardNo, 
							@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember,
							Model model, RedirectAttributes ra ) {
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("boardNo", boardNo);
		
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		Board board = service.selectBoardDetail(map);
		
		String path = null;
		
		if(board == null) {
			path = "redirect:/";
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		} else {
			
			// 조회수 증가 파트
			
			// --------------------------
			path = "board/boardDetail";
			
			model.addAttribute("board", board);
			model.addAttribute("gotoList", "/notice");
		}
		
		return path;
	}
	
	
	
	
	
	
	
	
	
}
