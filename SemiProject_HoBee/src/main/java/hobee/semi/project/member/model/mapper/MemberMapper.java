package hobee.semi.project.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.profileImg.model.dto.ProfileDTO;

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

	/**최신 프로필 이미지 조회
	 * @param memberNo
	 * @return
	 */
	ProfileDTO selectLatestProfile(int memberNo);
	
	/**아이디 찾기(이름)
	 * @param inputMember
	 * @return
	 */
	int checkName(MemberDTO inputMember);

	/** 아이디 찾기 (전화번호)
	 * @param inputMember
	 * @return
	 */
	int checkTel(MemberDTO inputMember);

	/** 아이디 찾기 결과 창으로 이동
	 * @param inputMember
	 * @return
	 */
	String findId(MemberDTO inputMember);

	/** 새 비빌번호 변경
	 * @param inputMember
	 * @return
	 */
	int pwChange(MemberDTO inputMember);

	/** 회원 프로필 조회
	 * @param memberNo
	 * @return
	 */
	MemberDTO selectMemberProfile(int memberNo);

	List<Board> selectMemberBoardList(int memberNo);

	List<String> selectMemberHobbyCode(int memberNo);

	
	
	
	
}