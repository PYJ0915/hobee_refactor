package hobee.semi.project.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
public class MemberDTO {
	private int memberNo;
	private String memberEmail;
	private String memberId;
	private String memberPw;
	private String memberNickame;
	private String memberTel;
	private String memberAddress;
	private String profileImg;
	private String enrollDate;
	private String memberDelfl;
	private int authority;
	
}
