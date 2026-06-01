package hobee.semi.project.challenge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cert {
	private int certNo;
	private int challengeNo;
	private int memberNo;
	private String certTitle;
	private String certContent;
	private String certWriteDate;
	private String memberNickname; // 목록 표시용
	private String profileImg; // 목록 표시용
}
