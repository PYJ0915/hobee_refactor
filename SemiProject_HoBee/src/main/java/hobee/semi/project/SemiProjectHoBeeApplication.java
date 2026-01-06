package hobee.semi.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // Spring Scheduler를 이용하기 위한 활성화 어노테이션
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SemiProjectHoBeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemiProjectHoBeeApplication.class, args);
	}

}
