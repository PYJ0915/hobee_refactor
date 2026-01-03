package hobee.semi.project.member.model.service;


import java.util.List;

import hobee.semi.project.member.model.dto.MemberDTO;

public interface MemberService {

	/** 로그인
	 * @param inputMember
	 * @return
	 */
	MemberDTO loginMember(MemberDTO inputMember);

	/** 이메일 중복검사
	 * @param memberEmail
	 * @return 숫자
	 */
	int checkEmail(String memberEmail);

	/** 아이디 중복 검사
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원가입
	 * @param inputMember
	 * @return
	 */
	int signUp(MemberDTO inputMember,List<String>memberAddress,List<String> hobbyCode);


}