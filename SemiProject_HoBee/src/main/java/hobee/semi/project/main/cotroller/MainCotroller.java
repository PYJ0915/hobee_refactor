package hobee.semi.project.main.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainCotroller {

	@RequestMapping("/")
	public String mainPage() {
		return "common/main";
	}
	
}
