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
         .addPathPatterns("/**")            
         .excludePathPatterns(
             // 로그인 / 로그아웃
             "/member/login",
             "/member/logout",
             // 제재 안내 페이지 (무한 리다이렉트 방지)
             "/penalty/**",
             "/footer/**",
             // 정적 자원
             "/css/**",
             "/js/**",
             "/images/**",
             "/favicon.ico",
             // 에러 페이지
             "/error",
             "/notification/**",
             "/follow/**"
         );
	}
	
}
