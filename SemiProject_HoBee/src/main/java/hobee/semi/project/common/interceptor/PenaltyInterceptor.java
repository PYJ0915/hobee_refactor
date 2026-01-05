package hobee.semi.project.common.interceptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.penalty.model.dto.Penalty;
import hobee.semi.project.penalty.model.service.PenaltyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class PenaltyInterceptor implements HandlerInterceptor {
	
	 @Autowired
	 private PenaltyService service;
	 
	 private String[] allowURLs = {"/member/login", "/member/logout", "/penalty"};
	 
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 로그인 하지 않았으면 세션 얻어오지 X
		HttpSession session = request.getSession(false);
		
		if (session == null) return true;
		
		String method = request.getMethod();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		 
		 // 로그인 안 했으면 제재 대상 X
		 if(loginMember == null) return true;
		 
		 // 예외 URL 통과
		 String requestURI = request.getRequestURI();
		 
		 for(String prefix : allowURLs) {
			 if(requestURI.startsWith(prefix)) {
				 return true;
			 }
		 }
		 
		 Penalty penalty = service.selectPenalty(loginMember.getMemberNo());
		 
		 // 제재 없으면 통과
		 if(penalty == null) {
			 return true;
		 }
		 
		 // 경고도 Interceptor에선 일단 통과 => 기간이 정해져있지 않아 1회성 보장이 힘들어질 수 있음
		 if("WARNING".equals(penalty.getPenaltyType())) {
			 return true;
		 }
		 
		 // 영구 정지는 무조건 차단
		 if("PERMANENT".equals(penalty.getPenaltyType())) {
			 response.sendRedirect("/penalty/permanent");
			 return false;
		 }
		 
		 // 정지는 기간이 만료되지 않았을 때만 차단
		 if ("SUSPEND".equals(penalty.getPenaltyType())) {
			 
			 // 기간 만료 => 만료 처리 후 통과
			 if(penalty.getPenaltyEndDate().isBefore(LocalDateTime.now())) {
				 service.expirePenalty(penalty.getPenaltyNo());
				 return true;
			 }
			 
			 // 기간 만료 X => 차단 (모든 행위 X, Post, Put, Delete만 차단)
			 if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
				 response.sendRedirect("/penalty/suspend");
				 return false;
			 }
			 
		 }
		 
		 return true;
		
	}

}
