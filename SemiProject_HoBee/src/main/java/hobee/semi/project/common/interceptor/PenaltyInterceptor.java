package hobee.semi.project.common.interceptor;

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
	 
	 private List<String> allowURLList = new ArrayList<>();
	 

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		allowURLList.add(0, "/penalty");
		allowURLList.add(1, "/member/login");
		allowURLList.add(2, "/member/logout");
		
		HttpSession session = request.getSession();
		MemberDTO loginMember = (MemberDTO)session.getAttribute("loginMember");
		 
		 // 로그인 안 했으면 제재 대상 X
		 if(loginMember == null) return true;
		 
		 
		 // 예외 URL 통과
		 String requestURI = request.getRequestURI();
		 
		 for(String prefix : allowURLList) {
			 if(requestURI.startsWith(prefix)) {
				 return true;
			 }
		 }
		 
		 Penalty penalty = service.selectPenalty(loginMember.getMemberNo());
		 

		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

}
