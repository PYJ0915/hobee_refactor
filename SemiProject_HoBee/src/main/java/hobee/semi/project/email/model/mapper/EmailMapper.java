package hobee.semi.project.email.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.email.model.dto.Email;

@Mapper
public interface EmailMapper {

	/** 이메일 인증 코드
	 * @param email
	 * @return
	 */
	int checkEmail (Email email);
 
	/** 인증 코드 업데이트
	 * @param email
	 * @return
	 */
	int updateAuthKey(Email email);

	/** 인증 코드 넣기
	 * @param email
	 * @return
	 */
	int insertAuthKey(Email email);

	/** 인증번호 확인
	 * @param authKey
	 * @return
	 */
	int checkAuthKey(Email email);

}
