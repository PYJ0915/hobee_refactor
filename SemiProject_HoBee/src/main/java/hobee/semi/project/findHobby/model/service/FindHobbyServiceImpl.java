package hobee.semi.project.findHobby.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.findHobby.model.dto.Question;
import hobee.semi.project.findHobby.model.dto.QuestionScore;
import hobee.semi.project.findHobby.model.mapper.FindHobbyMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class FindHobbyServiceImpl implements FindHobbyService{

	@Autowired
	private FindHobbyMapper mapper;
	
	@Override
	public List<Question> selectQuestionList() {
		return mapper.selectQuestionList();
	}
	
	@Override
	public List<QuestionScore> selectScore(int questionNo) {
		return mapper.selectScore(questionNo);
	}
	
	@Override
	public Map<String, Hobby> getHobby(String firstHobby, String secondHobby) {
		
		int firstHobbyCode = getHobbyCode(firstHobby);
		int secondHobbyCode = getHobbyCode(secondHobby);	
		
		Hobby firstHb = mapper.getHobby(firstHobbyCode);
		Hobby secondHb = mapper.getHobby(secondHobbyCode);
		
		if(firstHb == null || secondHb == null) return null;
		
		Map<String, Hobby> hobbyMap = new HashMap<>();
		
		hobbyMap.put("firstHobby", firstHb);
		hobbyMap.put("secondHobby", secondHb);	
		
		return hobbyMap;
	}
	
	public int getHobbyCode(String hobby) {
		
		int hobbyCode = 0;
		
		switch (hobby) {
		case "sports":
			hobbyCode = 1;
			break;
		case "art":
			hobbyCode = 2;
			break;
		case "selfDevelop":
			hobbyCode = 3;
			break;
		case "social":
			hobbyCode = 4;
			break;
		case "shopping":
			hobbyCode = 5;
			break;
		}
		
		return hobbyCode;
	}

}
