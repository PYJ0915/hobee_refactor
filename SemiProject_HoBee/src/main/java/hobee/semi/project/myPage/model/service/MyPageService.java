package hobee.semi.project.myPage.model.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.member.model.dto.MemberDTO;

public interface MyPageService {

	int updateInfo(MemberDTO inputMember, String[] memberAddress);

	int changePw(String newPw, String currentPw, MemberDTO loginMember);

	int profile(MultipartFile profileImg, MemberDTO loginMember) throws Exception;
	List<Hobby> selectHobbyList(List<String> hobbyCode);

}
