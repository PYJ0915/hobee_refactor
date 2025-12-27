package hobee.semi.project.findHobby.model.service;

import java.util.Map;

import hobee.semi.project.findHobby.model.dto.Hobby;

public interface FindHobbyService {

	Map<String, Hobby> getHobby(String firstHobby, String secondHobby);

}
