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

	/** 아이디 찾기 (가입된 이름 찾기)
	 * @param inputMember
	 * @return
	 */
	int checkName(MemberDTO inputMember);

	/** 아이디 찾기 (가입된 전화번호 찾기)
	 * @param inputMember
	 * @return
	 */
	int checkTel(MemberDTO inputMember);

	/** 아이디 찾기 결과 창으로 이동
	 * @param inputMember
	 * @return
	 */
	String findId(MemberDTO inputMember);

	/** 새 비밀번호
	 * @param inputMember
	 * @return
	 */
	int pwChange(MemberDTO inputMember);


}