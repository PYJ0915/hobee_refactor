package hobee.semi.project.findHobby.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.findHobby.model.dto.Question;
import hobee.semi.project.findHobby.model.dto.QuestionScore;

@Mapper
public interface FindHobbyMapper {

	List<Question> selectQuestionList();

	List<QuestionScore> selectScore(int questionNo);

	Hobby getHobby(int hobbyCode);
	
}
