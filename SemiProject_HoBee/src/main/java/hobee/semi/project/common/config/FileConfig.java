package hobee.semi.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

/**
 * @author 박세현
 * 
 * 
 *
 */
@Configuration // 설정 클래스
@PropertySource("classpath:/config.properties") // 외부에 있는 properties 소스를 잃어 가져오는 어노태이션)
public class FileConfig implements WebMvcConfigurer {

	// 데이터 임계값 설정 ( == config.properties 내용)

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

		MultipartConfigFactory factory = new MultipartConfigFactory();

		// 파일 업로드 임계값
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));

		// 임시 저장 폴더 경로 factory.setLocation(location);

		// HTTP 요청당 파일 최대 크기
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));

		// 개별 파일당 최대 크기 factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));

		return factory.createMultipartConfig();

	}

	@Bean
	public MultipartResolver multipartResolver() {

		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();

		return multipartResolver;

	}

	/*
	 * @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 * registry.addResourceHandler("클라이언트 요청 주소")
	 * .addResourceLocations("서버 컴퓨터 경로"); }
	 */

	@Value("${my.board.resource-handler}")
	private String boardResourceHandler;

	@Value("${my.board.resource-location}")
	private String boardResourceLocation;

	@Value("${my.chat.resource-handler}")
	private String chatResourceHandler;

	@Value("${my.chat.resource-location}")
	private String chatResourceLocation;

	@Value("${my.challenge.resource-handler}")
	private String challengeResourceHandler;

	@Value("${my.challenge.resource-location}")
	private String challengeResourceLocation;

	// -------------------------------------------

	// 프로필 이미지 관련 경로
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	// /myPage/profile/**

	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;
	// file:///C:/uploadFiles/profile/

	@Value("${my.profile.web-path}")
	private String profileWebPath;
	// MyPageServiceImpl

	@Value("${my.profile.folder-path}")
	private String profileFolderPath;
	// MyPageServiceImpl

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler(boardResourceHandler).addResourceLocations(boardResourceLocation);

		registry.addResourceHandler(profileResourceHandler).addResourceLocations(profileResourceLocation);

		registry.addResourceHandler("profileWebPath").addResourceLocations("profileFolderPath");

		// 채팅방 이미지 추가
		registry.addResourceHandler(chatResourceHandler).addResourceLocations(chatResourceLocation);

		registry.addResourceHandler(challengeResourceHandler).addResourceLocations(challengeResourceLocation);

	}

}
