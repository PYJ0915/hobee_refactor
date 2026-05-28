package hobee.semi.project.common.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 구독 경로 prefix
        registry.enableSimpleBroker("/topic", "/queue");
        // 클라이언트 → 서버 경로 prefix
        registry.setApplicationDestinationPrefixes("/app");
        // 1:1 메시지 경로 prefix
        registry.setUserDestinationPrefix("/user");
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
	}
	
	// 연결 시 세션의 닉네임을 Principal로 설정
    // → convertAndSendToUser() 에서 수신자 식별에 사용됨
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
        	@Override
        	public Message<?> preSend(Message<?> message, MessageChannel channel) {

        	    // wrap() 대신 getAccessor() 사용 → 변경 사항이 메시지에 직접 반영됨
        	    StompHeaderAccessor accessor =
        	        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        	    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
        	        String nickname = accessor.getFirstNativeHeader("nickname");
        	        System.out.println(">>> CONNECT nickname: " + nickname); // 확인용
        	        if (nickname != null && !nickname.isBlank()) {
        	            accessor.setUser(new UsernamePasswordAuthenticationToken(
        	                nickname, null, new ArrayList<>()
        	            ));
        	        }
        	    }
        	    return message;
        	}
        });
    }

}
