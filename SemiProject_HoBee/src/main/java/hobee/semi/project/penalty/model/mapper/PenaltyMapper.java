package hobee.semi.project.penalty.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.penalty.model.dto.Penalty;

@Mapper
public interface PenaltyMapper {

	Penalty selectPenalty(int memberNo);

	void expirePenalty(int penaltyNo);

	List<Penalty> selectPenaltyList(RowBounds rowBounds);

	int getPenaltyCount();

	int managePenalty(int penaltyNo);

}
