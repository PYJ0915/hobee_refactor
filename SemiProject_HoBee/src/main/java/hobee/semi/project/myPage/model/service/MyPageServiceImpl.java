package hobee.semi.project.myPage.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.myPage.model.mapper.MyPageMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@PropertySource("")
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Override
	public int updateInfo(MemberDTO inputMember, String[] memberAddress) {

		if(!inputMember.getMemberAddress().equals(",,")) {
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
			
			inputMember.setMemberAddress(null);
		}
		
		return mapper.updateInfo(inputMember);
	}
	
	
	@Override
	public int changePw(String newPw, String currentPw, MemberDTO loginMember) {

		String storePw = mapper.checkPw(loginMember.getMemberNo());
		
		if(!bcrypt.matches(currentPw,storePw)) return 0;
		
		String encPw = bcrypt.encode(newPw);
		loginMember.setMemberPw(encPw);
		
		return mapper.changePw(loginMember);
	}
	
}










