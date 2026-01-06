package hobee.semi.project.profileImg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
	
	private int profileNo;
	private String profilePath;
	private String profileOriginalName;
	private String profileRename;
	private String profileUpdateDate;
	private int memberNo;

}
