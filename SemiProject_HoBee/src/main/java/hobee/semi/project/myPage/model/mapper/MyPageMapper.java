package hobee.semi.project.myPage.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.findHobby.model.dto.Hobby;
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
	/** 회원 가입 시 선택한 취미 목록 조회 SQL 실행
	 * @param hobbyCode
	 * @return
	 */
	List<Hobby> selectHobbyList(List<String> hobbyCode);

	/** 기존에 선택한 취미 목록 제거 SQL 실행
	 * @param memberNo
	 */
	void deleteMemberHobby(int memberNo);

	/** 정보 수정 시 선택한 새로운 취미 목록 삽입 SQL 실행
	 * @param memberNo
	 * @param hobbyCode
	 */
	void insertMemberHobby(int memberNo, String hobbyCode);

}
