package hobee.semi.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*@Configuration
 * - 설정용 클래스임을 명시, 
 * 객체로 생성해서 내부 코드를 서버 실행 시 모두 수행
 * */

@Configuration
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();

		// BCryptPasswordEncoder : 평문을 BCrypt 패턴을 이용하여 암호화,
		// 평문과 암호하된 문자열을 비교해서 서로 맞는 값인지 판단 해줌

	}

}
