package hobee.semi.project.findHobby.model.service;

import java.util.List;
import java.util.Map;

import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.findHobby.model.dto.Question;
import hobee.semi.project.findHobby.model.dto.QuestionScore;

public interface FindHobbyService {

	List<Question> selectQuestionList();

	List<QuestionScore> selectScore(int questionNo);

	Map<String, Hobby> getHobby(String firstHobby, String secondHobby);
	
}
