package hobee.semi.project.common.filter;

import java.io.IOException;

import hobee.semi.project.member.model.dto.MemberDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// 권한등급을 보기위해 세션 객체 필요!
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		HttpSession session = req.getSession();
		
		if( ((MemberDTO)session.getAttribute("loginMember")).getAuthorLevel() != 2) {
			resp.sendRedirect("/adminError");
		} else {
			chain.doFilter(request, response);
		}
		
	}

}
