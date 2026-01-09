package hobee.semi.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

	private int commentNo;
	private String commentContent;
	private String commentWriteDate;
	private String commentDelFl;
	private int boardNo;
	private int memberNo;
	private int commentNo2;
	
	private String memberNickname;
	private String profileImg;
	
	private int authorLevel; // 관리자 신고를 막기 위한 권한 등급
	
}
