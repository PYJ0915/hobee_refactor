package hobee.semi.project.email.model.service;

public interface EmailService {

	

	/** 인증코드 보내기
	 * @param string
	 * @param authEmail
	 * @return
	 */
	int checkEmail (String type, String authEmail);

	/** 인증번호 확인
	 * @param string
	 * @param authKey
	 * @return
	 */
	int checkAuthKey(String authKey,String authEmail);



}
