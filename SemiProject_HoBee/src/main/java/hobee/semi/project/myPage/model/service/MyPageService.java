package hobee.semi.project.myPage.model.service;

import hobee.semi.project.member.model.dto.MemberDTO;

public interface MyPageService {

	int updateInfo(MemberDTO inputMember, String[] memberAddress);

	int changePw(String newPw, String currentPw, MemberDTO loginMember);

}
