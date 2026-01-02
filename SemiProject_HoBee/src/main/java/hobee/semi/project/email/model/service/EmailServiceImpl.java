package hobee.semi.project.email.model.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.email.model.dto.Email;
import hobee.semi.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

	
	@Autowired
    private EmailMapper mapper;
	
	@Autowired
	private JavaMailSender mailSender; // 메일 발송 객체(우체국)
	
	// 인증 코드 보내기
	@Override
	public int checkEmail (String type, String authEmail) {
		
		// 난수 생성 인증코드
		String authKey = createAuthKey();
		
		Email email = new Email();
		email.setAuthEmail(authEmail); // 이메일
		email.setAuthKey(authKey); // 인증코드 
		
		int count = mapper.checkEmail (email); // 이메일,인증코드 DB에 저장 시키러 ㄱ
		
		int result;
		
	    if (count > 0) {
	        // 이미 있으면 업데이트 (여러번 눌렀을 때 마지막 코드로 업데이트)
	        result = mapper.updateAuthKey(email);
	    } else {
	        // 없으면 새로 삽입(처음 일 때)
	        result = mapper.insertAuthKey(email);
	    }
		
	    
	    
		if(result > 0) { // 인증번호 코드가 있다면
			try {
				
				// 매일 전송 객체 생성
				MimeMessage mail = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8"); 
				//  MimeMessageHelper helper:  위 코드 도와주는 코드(간편하게 메일 보낼 때 도와줌)
				
				// 메일 세팅
				helper.setTo(authEmail); // 사용자가 입력한 이메일
                helper.setSubject("[Hobee] 회원가입 인증번호입니다."); // 제목
				
                String content ="인증번호 : "+authKey;
                helper.setText(content);
                
                mailSender.send(mail);
                
                return 1;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		return 0;
	}


	
	
	// 인증번호 발급메서드(난수 생성 6글자)
	private String createAuthKey() {
		return UUID.randomUUID().toString().substring(0,6);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	
}
