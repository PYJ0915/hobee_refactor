package hobee.semi.project.board.model.service;

import java.util.List;

import hobee.semi.project.board.model.dto.Comment;

public interface CommentService {

	List<Comment> select(int boardNo);

	int insert(Comment comment);

	int delete(int commentNo);

	int update(Comment comment);

}
