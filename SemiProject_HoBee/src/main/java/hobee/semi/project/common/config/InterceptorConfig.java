package hobee.semi.project.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hobee.semi.project.common.interceptor.PenaltyInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	@Autowired
	private PenaltyInterceptor penaltyInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(penaltyInterceptor)
		// 글쓰기, 댓글, 신고, 좋아요 관련 요청만 막기
		.addPathPatterns("/editBoard/**", "/comment/**", "/report/**", "/free/like", "/notice/like", "/hobby/like") 
		.excludePathPatterns("/css/**" , "/js/**", "/images/**", "/favicon.ico/**", "/member/login", "/member/logout" );
	}
	
}
