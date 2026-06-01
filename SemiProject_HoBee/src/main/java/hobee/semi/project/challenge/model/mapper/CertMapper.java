package hobee.semi.project.challenge.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import hobee.semi.project.challenge.model.dto.Cert;

@Mapper
public interface CertMapper {

	int insertCert(Cert cert);

	List<Cert> selectCertList(@Param("challengeNo") int challengeNo, @Param("memberNo") int memberNo);

}
