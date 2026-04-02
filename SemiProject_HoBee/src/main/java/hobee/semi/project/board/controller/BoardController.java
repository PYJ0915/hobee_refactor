package hobee.semi.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import hobee.semi.project.board.model.service.BoardService;
import hobee.semi.project.member.model.dto.MemberDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
public class BoardController {

	@Autowired
	private BoardService service;

	@GetMapping({ "list/{boardCode:[0-9]+}", "list/{boardCode:[0-9]+}/{categoryCode:[0-9]+}" })
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
			@PathVariable(name = "categoryCode", required = false) Integer categoryCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@RequestParam Map<String, Object> paramMap) {

		String url = getBoardUrl(boardCode);

		// 취미 게시판인 경우 해야할 작업
		if (categoryCode != null) {

			log.debug("취미 게시판 이름 조회 시작");
			String categoryName = service.selectCategoryName(categoryCode);
			log.debug("취미 게시판 이름 조회 완료 {}", categoryName);

			model.addAttribute("categoryCode", categoryCode);
			model.addAttribute("hobbyName", categoryName);
		}

		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;

		// 검색이 아닌 경우 --> paramMap 은 {}
		if (paramMap.get("key") == null) {

			// 게시글 목록 조회 서비스 호출
			log.debug("게시글 목록 조회 시작");
			map = service.selectBoardList(boardCode, categoryCode, cp);
			log.debug("게시글 목록 조회 완료 {}", map);

		} else { // 검색인 경우
			// --> paramMap에 key라는 k에 접근하면 매핑된 value 반환
			// --> ex) {key=w, query=짱구}
			// --> --> w 반환됨

			// boardCode를 paramMap에 추가
			paramMap.put("boardCode", boardCode);
			// -> paramMap은 {key=w, query=짱구, boardCode=1}

			paramMap.put("categoryCode", categoryCode);

			// 검색(내가 검색한 게시글 목록 조회) 서비스 호출
			log.debug("검색한 게시글 목록 조회 시작");
			map = service.searchList(paramMap, cp);
			log.debug("검색한 게시글 목록 조회 완료 {}", map);

		}

		// 자유 및 취미 게시판에서 해야하는 작업
		if (boardCode != 1) {
			log.debug("인기 게시글 및 공지사항 조회 시작");
			List<Board> bestList = service.selectBestList(boardCode, categoryCode);
			List<Board> noticeList = service.noticeList(1);
			log.debug("인기 게시글 및 공지사항 조회 완료 {}, {}", bestList, noticeList);

			model.addAttribute("bestList", bestList);
			model.addAttribute("noticeList", noticeList);

		}

		// model에 결과 값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("boardCode", boardCode);

		// src/main/resources/templates/board/boardList.html 로 forward
		return url;
	}

	/**
	 * 내가 작성한 게시물만 보여주는 버튼 구현
	 * 
	 * @param cp
	 * @param model
	 * @param paramMap
	 * @param loginMember
	 * @return
	 */
	@GetMapping({ "{boardCode:[0-9]+}/myBoard", "{boardCode:[0-9]+}/{categoryCode:[0-9]+}/myBoard" })
	public String myBoardList(@PathVariable("boardCode") int boardCode,
			@PathVariable(name = "categoryCode", required = false) Integer categoryCode,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember, RedirectAttributes ra) {

		String url = getBoardUrl(boardCode);

		Map<String, Object> queryMap = new HashMap<>();

		if (categoryCode != null) {
			String categoryName = service.selectCategoryName(categoryCode);

			queryMap.put("categoryCode", categoryCode);

			model.addAttribute("categoryCode", categoryCode);
			model.addAttribute("hobbyName", categoryName);
		}

		queryMap.put("boardCode", boardCode);

		if (loginMember != null) {
			queryMap.put("memberNo", loginMember.getMemberNo());
		} else {
			ra.addFlashAttribute("message", "로그인 후 이용 가능한 서비스입니다.");
			// 원래 있던 게시판 목록 주소로 리다이렉트 (예: /hobby/1)
			return "redirect:/board/list/" + boardCode;
		}

		// 2. 서비스 호출 (생성한 queryMap을 전달)
		log.debug("내 게시글 목록 조회 시작");
		Map<String, Object> map = service.selectMyBoardList(queryMap, cp);
		log.debug("내 게시글 목록 조회 완료 {}", map);

		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		model.addAttribute("boardCode", boardCode);

		// 3. 게시판 목록을 보여주는 HTML 파일명
		return url;
	}

	/**
	 * 게시글 상세 조회 함수
	 * 
	 */
	@GetMapping({ "detail/{boardNo:[0-9]+}", "detail/{categoryCode:[0-9]+}/{boardNo:[0-9]+}" })
	public String boardDetail(@PathVariable(name = "categoryCode", required = false) Integer categoryCode,
			@PathVariable("boardNo") int boardNo,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember, Model model,
			RedirectAttributes ra, HttpServletRequest req, HttpServletResponse resp) {

		Map<String, Object> map = new HashMap<>();

		map.put("boardNo", boardNo);

		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		log.debug("게시글 상세 조회 시작");
		Board board = service.selectBoardDetail(map);
		log.debug("게시글 상세 조회 완료 {}", board);

		if (board == null) {
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			return "redirect:/";
		}

		handleViewCount(board, boardNo, loginMember, req, resp);

		model.addAttribute("board", board);
		model.addAttribute("gotoList", "/notice");

		return "board/boardDetail";
	}

	@ResponseBody
	@PostMapping("like")
	public int boardLike(@RequestBody Map<String, Integer> map) {
		return service.boardLike(map);
	}

	private String getBoardUrl(int boardCode) {

		String url = "board/";

		switch (boardCode) {
		case 1:
			url += "noticeBoard";
			break;
		case 2:
			url += "hobbyBoard";
			break;
		case 3:
			url += "freeBoard";
			break;
		}

		return url;
	}

	/**
	 * 조회수 증가 함수
	 * 
	 */
	private void handleViewCount(Board board, int boardNo, MemberDTO loginMember, HttpServletRequest req,
			HttpServletResponse resp) {

		if (loginMember == null || board.getMemberNo() != loginMember.getMemberNo()) {
			Cookie[] cookies = req.getCookies();
			if (cookies == null)
				cookies = new Cookie[0];
			Cookie c = null;

			for (Cookie temp : cookies) {
				// 쿠키 중에 "readBoardNo" 가 존재할 때
				if (temp.getName().equals("readBoardNo")) {
					c = temp;
					break;
				}
			}

			int result = 0; // 조회수 증가 결과 저장 변수

			log.debug("조회수 증가 시작");
			if (c == null) {
				// "readBoardNo" 가 쿠키에 없을 때
				c = new Cookie("readBoardNo", "[" + boardNo + "]");
				result = service.updateViewCount(boardNo);
			} else {

				if (c.getValue().indexOf("[" + boardNo + "]") == -1) {
					c.setValue(c.getValue() + "[" + boardNo + "]");
					result = service.updateViewCount(boardNo);

				}
			}

			if (result > 0) {
				board.setBoardViewCount(result);
				c.setPath("/");
				long secondsUntilNextDay = calcSecondsUntilMidnight();
				c.setMaxAge((int) secondsUntilNextDay);
				resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
				log.debug("조회수 증가 완료");
			}

		}

	}

	/**
	 * 자정까지 남은 초 계산 (handleViewCount 안에서 호출)
	 * 
	 * @return
	 */
	private long calcSecondsUntilMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
		return Duration.between(now, nextDayMidnight).getSeconds();
	}

}
