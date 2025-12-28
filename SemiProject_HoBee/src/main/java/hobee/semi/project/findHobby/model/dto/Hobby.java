package hobee.semi.project.findHobby.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Hobby {

	private int hobbyCode;
	private String hobbyName;
	private String hobbyCategory;
	private String hobbyIcon;
	private int hobbyParentCode;
	private int hobbyLevel;
	private int boardCode;
	
	// 같은 카테고리의 취미 리스트 (취미 탐색 결과용)
	private List<Hobby> hobbyList;
}
