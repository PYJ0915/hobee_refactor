package hobee.semi.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(NoResourceFoundException.class)
	public String notFound(NoResourceFoundException e, HttpServletRequest req) {
		log.warn("404 Not Found - URI: {}", req.getRequestURI());
		return "error/404";
	}

	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Exception e, HttpServletRequest req, Model model) {
		log.error("500 서버 오류 - URI: {}", req.getRequestURI(), e);
		model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다.");
		return "error/500";
	}
}
