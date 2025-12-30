package hobee.semi.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.member.model.mapper.MemberMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService{

	@Autowired
	private  MemberMapper mapper;
	
	@Autowired 
	private BCryptPasswordEncoder bcrypt;
	
	// 로그인
	@Override
	public MemberDTO loginMember(MemberDTO inputMember) {
		// 들고온 input 값 중 비밀번호 암호화해서 비교하기
		
		// db에서 암화화된 비밀번호를 가져와서 로그인한 비밀번호랑 비교
		
		// 암호화 비번(게시판 등 사용 가능하기 때문에 비번만 가지고 오지말고 다 갖고오기)
		MemberDTO loginMember = mapper.loginMember(inputMember);
		
		// input 값 암화화 값 비교 
		// 일치하지 않을때
		if(loginMember == null) {
			return null; // 로그인 실패 시 null 로 반환
		}
		
		if(!bcrypt.matches(inputMember.getMemberPw(),loginMember.getMemberPw() )) {
			return null;
		}
		
		// 비번이 맞을 경우 (보안상 비번은 null로 변경)
		loginMember.setMemberPw(null);
		
		// DB에 저장된 회원 값을 갖고 리턴 즉 로그인 성공 후 세션에 저장
		return loginMember;
	}

	// 이메일 중복 검사
	@Override
	public int checkEmail(String memberEmail) {
		return  mapper.checkEmail(memberEmail);
	}

	// 아이디 중복검사
	@Override
	public int checkId(String memberId) {
		return mapper.checkId(memberId);
	}
	

}