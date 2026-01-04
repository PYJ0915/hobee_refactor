package hobee.semi.project.myPage.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.member.model.dto.MemberDTO;

@Mapper
public interface MyPageMapper {

	/** 회원 정보 수정 SQL 실행
	 * @param inputMember
	 * @return
	 */
	int updateInfo(MemberDTO inputMember);

	/** 기존 비밀번호 확인 SQL 실행 
	 * @param memberNo
	 * @return
	 */
	String checkPw(int memberNo);

	/** 비밀번호 변경 SQL 실행
	 * @param loginMember
	 * @return
	 */
	int changePw(MemberDTO loginMember);

	/**프로필 이미지 변경 SQL 실행
	 * @param member
	 * @return
	 */
	int profile(MemberDTO member);

}
