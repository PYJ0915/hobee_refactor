package hobee.semi.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.board.model.service.HobbyBoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("hobby")
@Slf4j
@RequiredArgsConstructor
public class HobbyBoardController {
	
	private final HobbyBoardService service;

	/** 내가 작성한 게시물만 보여주는 버튼 구현
	 * @param cp  
	 * @param model
	 * @param paramMap
	 * @param loginMember
	 * @return
	 */
	@GetMapping("{categoryCode:[0-9]+}/myBoard")
	public String myBoardList(
			@PathVariable("categoryCode") int categoryCode,
            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp, 
            Model model,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember,
			RedirectAttributes ra) {
		
		String categoryName = service.selectCategoryName(categoryCode);		
		
	    if(categoryName == null) {
	        // 강제로 hobby/1 주소로 다시 보내버림
	        return "redirect:/hobby/1";
	    }
	    
	    
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("categoryCode", categoryCode);
		// 로그인 여부 체크

		if(loginMember != null) {
			queryMap.put("memberNo", loginMember.getMemberNo());
		} else {
			ra.addFlashAttribute("message", "로그인 후 이용 가능한 서비스입니다.");
	        // 원래 있던 게시판 목록 주소로 리다이렉트 (예: /hobby/1)
	        return "redirect:/hobby/" + categoryCode;
		}
		
		// 2. 서비스 호출 (생성한 queryMap을 전달)
		Map<String, Object> map = service.selectMyBoardList(categoryCode, cp, queryMap);
		
		model.addAttribute("categoryCode",categoryCode);
		model.addAttribute("hobbyName", categoryName);
		model.addAttribute("pagination", map.get("pagination"));
	    model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("hobbyBestList", service.hobbyBestList(categoryCode));
		model.addAttribute("noticeList", service.noticeList(1));
		
		// 3. 게시판 목록을 보여주는 HTML 파일명
		return "board/hobbyBoard"; 
	}
	
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
							Model model, RedirectAttributes ra, HttpServletRequest req, HttpServletResponse resp ) {
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("boardNo", boardNo);
		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		Board board = service.selectBoardDetail(map);
		
		String path = null;
		
		if(board == null) {
			path = "redirect:/";
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		} else {
			
			// 조회수 증가 파트
			
			// 조회 수가 증가해야하는 경우
			if(loginMember == null || board.getMemberNo() != loginMember.getMemberNo()) {
				
				Cookie[] cookies = req.getCookies();
				
				Cookie c = null;
				
				for(Cookie temp : cookies) {
					
					// 쿠키 중에 "readBoardNo" 가 존재할 때
					if(temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
					
				}
				
				int result = 0; // 조회수 증가 결과 저장 변수
				
				if(c == null) {
					// "readBoardNo" 가 쿠키에 없을 때
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					
					result = service.updateViewCount(boardNo);
				} else {
					
					if(c.getValue().indexOf("[" + boardNo + "]") == -1) {
						
						c.setValue(c.getValue() + "[" + boardNo + "]");
						result = service.updateViewCount(boardNo);
						
					}
				}
				
				if(result > 0) {
					
					board.setBoardViewCount(result);
					
					c.setPath("/");
					
					LocalDateTime now = LocalDateTime.now();
					
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					
					c.setMaxAge((int)secondsUntilNextDay);
					
					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
					
				}
				
			}
			
			
			// --------------------------
			
			path = "board/boardDetail";
			
			model.addAttribute("board", board);
			model.addAttribute("gotoList", "/hobby/" + categoryCode);
		}
		
		return path;
	}
	
	@ResponseBody
	@PostMapping("like")
	public int boardLike(@RequestBody Map<String, Integer> map) {
		return service.boardLike(map);
	}
	
	
	
	
}
