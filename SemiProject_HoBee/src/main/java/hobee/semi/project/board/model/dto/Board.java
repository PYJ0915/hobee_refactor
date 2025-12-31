package hobee.semi.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int boardViewCount;
	private String boardDelFl;
	private int memberNo;
	private int boardCode;
	
	private String memberNickname;
	
	// 목록 조회 시 서브쿼리 필드
	private int commentCount; // 댓글 수
	private int likeCount;    // 좋아요 수
	
	private int categoryCode;   // 카테코리 코드 ( 1 2 3 4 5)
	private String categoryName; // 카테고리 이름
	
}
