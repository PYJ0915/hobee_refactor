package hobee.semi.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SemiProjectHoBeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemiProjectHoBeeApplication.class, args);
	}

}
