package hobee.semi.project.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.HobbyBoardService;
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
	
	
	
	
	
	
}
