package hobee.semi.project.email.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
public class Email {

	private int authKeyCode;    // PK 
    private String authEmail;   // 사용자 이메일
    private String authKey;     // 인증번호 6자리
    private String authKeyDate; // 생성일 
}
