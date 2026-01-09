package hobee.semi.project.footer.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import hobee.semi.project.footer.model.dto.CS;

@Mapper
public interface FooterMapper {

	int insertCS(CS cs);

	int getCSCount();

	List<CS> selectCSList(RowBounds rowBounds);

	CS selectTarget(int csNo);

	int csComplete(int csNo);
	
}
