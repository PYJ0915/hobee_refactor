package hobee.semi.project.follow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {
	
	private int followNo;
    private int followerNo;   // 팔로우 하는 사람
    private int followingNo;  // 팔로우 받는 사람
    private String followDate;
    private String memberNickname; // 목록 표시용
    private String profileImg;     // 목록 표시용

}
