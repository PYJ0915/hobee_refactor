package hobee.semi.project.findHobby.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.findHobby.model.dto.Hobby;

@Mapper
public interface FindHobbyMapper {

	Hobby getHobby(int hobbyCode);

}
