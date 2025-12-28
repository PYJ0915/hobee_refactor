package hobee.semi.project.findHobby.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QuestionScore {
	
	private int questionNo;
	private int hobbyCode;
	private int answerScore;
	
}
