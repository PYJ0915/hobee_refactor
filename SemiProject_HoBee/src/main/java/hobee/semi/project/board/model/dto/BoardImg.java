package hobee.semi.project.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardImg {

	private int BoardImgNo;
	private String BoardImgPath;
	private String BoardImgOriginalName;
	private String BoardImgRename;
	private int BoardNo;
	
}
