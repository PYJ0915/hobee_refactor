package hobee.semi.project.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	/** 로그인
	 * @param inputMember 
	 * @return
	 */
	MemberDTO loginMember(MemberDTO inputMemberinputMember);

	
	
	
	
}