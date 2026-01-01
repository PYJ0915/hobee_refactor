package hobee.semi.project.board.controller;

import java.util.HashMap;
import java.util.List;
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
import hobee.semi.project.board.model.service.HobbyBoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("hobby")
@Slf4j
@RequiredArgsConstructor
public class HobbyBoardController {
	
	private final HobbyBoardService service;
	
	@GetMapping("{categoryCode:[0-9]+}") 
    public String selectBoardList(
                @PathVariable("categoryCode") int categoryCode,
                @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
                Model model,
                @RequestParam Map<String, Object> paramMap) {
		
		int noticeBoardCode = 1;
		
		String categoryName = service.selectCategoryName(categoryCode);
		
	    if(categoryName == null) {
	        // 강제로 hobby/1 주소로 다시 보내버림
	        return "redirect:/hobby/1";
	    }
		
		
		
		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;
		
		// 검색이 아닌 경우 --> paramMap 은 {}
		if(paramMap.get("key") == null) {
			
			map = service.selectBoardList(categoryCode, cp);
	
		} else { 

			paramMap.put("categoryCode", categoryCode);
			
			map = service.searchList(paramMap, cp);
			
		}
		
		List<Board> hobbyBestList = service.hobbyBestList(categoryCode);
		
		List<Board> noticeList = service.noticeList(noticeBoardCode);
		
		
		// model에 결과 값 등록
		model.addAttribute("categoryCode", categoryCode);   
        model.addAttribute("hobbyName", categoryName);       
        model.addAttribute("pagination", map.get("pagination"));
        model.addAttribute("boardList", map.get("boardList"));
        model.addAttribute("hobbyBestList", hobbyBestList);
        model.addAttribute("noticeList", noticeList);
		
		
		return "board/hobbyBoard";
	}
	
	@GetMapping("{categoryCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardNo") int boardNo,
							@PathVariable("categoryCode") int categoryCode,
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
			model.addAttribute("gotoList", "/hobby/" + categoryCode);
		}
		
		return path;
	}
	
	
	
	
	
	
}
