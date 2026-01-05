package hobee.semi.project.penalty.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.penalty.model.dto.Penalty;

@Mapper
public interface PenaltyMapper {

	Penalty selectPenalty(int memberNo);

	void expirePenalty(int penaltyNo);

}
