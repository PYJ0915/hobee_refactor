package hobee.semi.project.email.model.service;

public interface EmailService {

	

	/** 인증코드 보내기
	 * @param string
	 * @param authEmail
	 * @return
	 */
	int checkEmail (String type, String authEmail);



}
