package hobee.semi.project.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	/** 로그인
	 * @param inputMember 
	 * @return
	 */
	MemberDTO loginMember(MemberDTO inputMemberinputMember);

	/** 이메일 중복 검사
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 아이디 중복 검사
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId);

	/** 닉네임 중복 검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원가입
	 * @param inputMember
	 * @return
	 */
	int signUp(MemberDTO inputMember);

	/** 여러개 취미 선택
	 * @param map
	 */
	void insertMemberHobby(Map<String, Object> map);

	
	
	
	
}