package hobee.semi.project.member.model.service;

import java.beans.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.member.model.mapper.MemberMapper;
import hobee.semi.project.profileImg.model.dto.ProfileDTO;

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

	// 닉네임 중복검사
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}

	// 회원가입
	@Override
	public int signUp(MemberDTO inputMember,List<String> memberAddress,List<String> hobbyCodes) {
		
		String encPw = bcrypt.encode(inputMember.getMemberPw()); // 비밀번호 암화해서 넣기
		inputMember.setMemberPw(encPw);
		
		if(memberAddress != null) {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address); // ^^^으로 join하고 넣어주기
		}else {
			inputMember.setMemberAddress(null); // 필수아님
		}
		
		int result = mapper.signUp(inputMember);

		if(result > 0 && hobbyCodes != null && !hobbyCodes.isEmpty()) {
	        for(String hobbyCode : hobbyCodes) {
	            // 매퍼에 넘길 파라미터 맵 생성 (또는 별도의 DTO 사용 가능)
	            Map<String, Object> map = new HashMap<>();
	            map.put("memberNo", inputMember.getMemberNo()); // 방금 생성된 시퀀스 번호
	            map.put("hobbyCode", hobbyCode);
	            
	            
	            
	            mapper.insertMemberHobby(map);
	        }
	    }
	    
	    return result;
	}

	// 아이디 찾기(이름 일치)
	@Override
	public int checkName(MemberDTO inputMember) {
		
		int result = mapper.checkEmail(inputMember.getMemberEmail()); // 해당 이메일 있으면 1
		
		if(result == 0) { // 해당 이메일이 없을 경우
			return -1;
		}
		return mapper.checkName(inputMember);
	}

	// 아이디 찾기(전화번호  일치)
	@Override
	public int checkTel(MemberDTO inputMember) {
		int result = mapper.checkEmail(inputMember.getMemberEmail()); // 해당 이메일 있으면 1
		
		if(result == 0) { // 해당 이메일이 없을 경우
			return -1;
		}
		return  mapper.checkTel(inputMember);
	}

	// 아이디 찾기 결과 값 창으로 이동
	@Override
	public String findId(MemberDTO inputMember) {

		return mapper.findId(inputMember);
	}

	// 새 비밀번호
	@Override
	public int pwChange(MemberDTO inputMember) {
		
		String encPw = bcrypt.encode(inputMember.getMemberPw()); // inputPw 암호화 하기
		
		inputMember.setMemberPw(encPw); // 암호화 하고 짚어 넣기
		
		
		return mapper.pwChange(inputMember);
	}
	
	//최신 프로필 이미지 조회
	@Override
	public ProfileDTO selectLatestProfile(int memberNo) {
		ProfileDTO profile = mapper.selectLatestProfile(memberNo);
		return profile;
	}
	

}