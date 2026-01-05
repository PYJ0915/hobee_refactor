package hobee.semi.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// ServletRequest : HttpServletRequest의 부모 타입
		// ServletResponse : HttpServletResponse의 부모 타입

		// FilterChain : 다음 필터 또는 DispatcherServlet과 연결된 객체

		// session이 필요함 => why? loginMember가 session에 담김
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		// 클라이언트가 현재 요청중인 주소를 가져옴
		String path = req.getRequestURI();
		
		// 로그인을 하지 않았더라도 다른 사람의 프로필 이미지는 볼 수 있도록
		// "/myPage/profile"로 시작하는 요청은 필터 통과
		if(path.startsWith("/myPage/profile/")) {
			chain.doFilter(request, response);
			return;
		}
		
		// 로그인 체크용 세션 얻어오기
		HttpSession session = req.getSession();
		
		if(session.getAttribute("loginMember") == null) {
			resp.sendRedirect("/loginError");
		} else {
			chain.doFilter(request, response);
		}
		
	}

}
