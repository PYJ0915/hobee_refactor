package hobee.semi.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

/**
 * @author 박세현
 * 
 * 
 * 
 */
@Configuration // 설정 클래스
@PropertySource("classpath:/config.properties") // 외부에 있는 properties 소스를 잃어 가져오는 어노태이션)
public class FileConfig {
	
	//데이터 임계값 설정 ( == config.properties 내용)
	
	// 파일 업로드 임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold; // 52428800

	// 임계값 초과시 파일의 임시 저장경로
	@Value("${spring.servlet.multipart.location}")
	private String location; // C:/uploadFiles/temp/

	// 요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize; // 52428800

	// 개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize; // 10485760
	
	
	// MultipartResolver 설정
		@Bean
		public MultipartConfigElement configElement() {
			
			MultipartConfigFactory factory 
				= new MultipartConfigFactory();
			
			// 파일 업로드 임계값
			factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
			
			// 임시 저장 폴더 경로
			factory.setLocation(location);
			
			// HTTP 요청당 파일 최대 크기
			factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
			
			// 개별 파일당 최대 크기
			factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
			
			return factory.createMultipartConfig();
		}
		
		
		@Bean
		public MultipartResolver multipartResolver() {
			
			StandardServletMultipartResolver multipartResolver
				= new StandardServletMultipartResolver();
			
			return multipartResolver;
			
		}


}
