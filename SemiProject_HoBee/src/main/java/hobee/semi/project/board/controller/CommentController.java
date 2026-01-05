package hobee.semi.project.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hobee.semi.project.board.model.dto.Comment;
import hobee.semi.project.board.model.service.CommentService;

@RestController
@RequestMapping("comment")
public class CommentController {
	
	@Autowired
	private CommentService service;
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	@GetMapping("")
	public List<Comment> select(@RequestParam("boardNo") int boardNo) {
		
		return service.select(boardNo);
	}

	/** 댓글/답글(대댓글) 등록
	 * & 답글인 경우 commentNo2 포함
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		
		return service.insert(comment);
	}
	
	/** 댓글 삭제
	 * @return
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		
		return service.delete(commentNo);
	}
	
	/** 댓글 수정
	 * @return
	 */
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
	
		return service.update(comment);
	}
}










