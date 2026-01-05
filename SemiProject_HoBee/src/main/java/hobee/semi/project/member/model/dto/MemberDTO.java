package hobee.semi.project.member.model.dto;

import java.util.List;

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
	private String memberName;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String enrollDate;
	private int authorLevel;
	private String memberDelfl;
	private String memberIntroduction;
	private String profileImg;
	
	private List<String> hobbyCode;
}
