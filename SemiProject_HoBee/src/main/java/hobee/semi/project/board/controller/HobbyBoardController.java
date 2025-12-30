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
	
	@GetMapping("{hobbyCode:[0-9]+}") 
    public String selectBoardList(
                @PathVariable("hobbyCode") int hobbyCode,
                @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
                Model model,
                @RequestParam Map<String, Object> paramMap) {
		
		int boardCode = hobbyCode;
		int noticeBoardCode = 1;
		
		String hobbyName = "";
        switch(hobbyCode) {
            case 1: hobbyName = "운동·레저"; break;
            case 2: hobbyName = "자기 계발"; break;
            case 3: hobbyName = "문화·예술"; break;
            case 4: hobbyName = "사회 교류"; break;
            case 5: hobbyName = "수집·소비"; break;
            default: hobbyName = "취미";
        }
        
		
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
		
		List<Board> hobbyBestList = service.hobbyBestList(boardCode);
		
		List<Board> noticeList = service.noticeList(noticeBoardCode);
		
		
		// model에 결과 값 등록
		model.addAttribute("hobbyCode", hobbyCode);
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("boardCode", boardCode);
		model.addAttribute("hobbyName", hobbyName);
		model.addAttribute("hobbyBestList", hobbyBestList);
		model.addAttribute("noticeList", noticeList);
		
		
		return "board/hobbyBoard";
	}
	
	
	
	
	
	
}
