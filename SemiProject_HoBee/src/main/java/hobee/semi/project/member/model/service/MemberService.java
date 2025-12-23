package hobee.semi.project.member.model.service;


import hobee.semi.project.member.model.dto.MemberDTO;

public interface MemberService {

	/** 로그인
	 * @param inputMember
	 * @return
	 */
	MemberDTO loginMember(MemberDTO inputMember);


}
