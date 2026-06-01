package hobee.semi.project.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Comment;
import hobee.semi.project.board.model.mapper.BoardMapper;
import hobee.semi.project.board.model.mapper.CommentMapper;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

	private final CommentMapper mapper;
	private final BoardMapper boardMapper;
    private final NotificationMapper notificationMapper;
	
	/** 댓글 목록 조회 서비스
	 *
	 */
	@Override
	public List<Comment> select(int boardNo) {
		return mapper.select(boardNo);
	}
	
	/** 댓글/답글 등록 서비스
	 *
	 */
	@Override
	public int insert(Comment comment) {
		
		int result = mapper.insert(comment);

        if (result > 0) {
            // 게시글 작성자 조회
            int boardMemberNo = boardMapper.selectBoardMemberNo(comment.getBoardNo());

            // 본인 게시글에 본인이 댓글 달면 알림 X
            if (boardMemberNo != comment.getMemberNo()) {
                Notification noti = Notification.builder()
                    .receiverNo(boardMemberNo)
                    .senderNo(comment.getMemberNo())
                    .notiType("COMMENT")
                    .notiTargetNo(comment.getBoardNo())
                    .notiMessage("님이 회원님의 게시글에 댓글을 달았습니다.")
                    .build();
                notificationMapper.insertNotification(noti);
            }
        }
		
		return result;
	}
	
	/** 댓글 삭제 서비스
	 *
	 */
	@Override
	public int delete(int commentNo) {
		return mapper.delete(commentNo);
	}
	
	/** 댓글 수정 서비스
	 *
	 */
	@Override
	public int update(Comment comment) {
		return mapper.update(comment);
	}
	
	
}