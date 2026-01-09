package hobee.semi.project.myPage.model.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.profileImg.model.dto.ProfileDTO;

public interface MyPageService {

	int updateInfo(MemberDTO inputMember, String[] memberAddress);

	int changePw(String newPw, String currentPw, MemberDTO loginMember);

	int profile(MultipartFile profileImg, MemberDTO loginMember) throws Exception;
	
	List<Hobby> selectHobbyList(List<String> hobbyCode);

	List<Board> selectBoardList(int memberNo);

	int checkNickname(String memberNickname, int memberNo);

	int secession(String memberPw, int memberNo);

}
