package hobee.semi.project.myPage.model.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import hobee.semi.project.common.util.Utility;
import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.myPage.model.mapper.MyPageMapper;

@Service
@Transactional(rollbackFor = Exception.class)
//@Slf4j
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath;
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath;
	
	@Override
	public List<Hobby> selectHobbyList(List<String> hobbyCode) {
		
        if (hobbyCode == null || hobbyCode.isEmpty()) {
            
        	return List.of();
        }
		
		return mapper.selectHobbyList(hobbyCode);
	}
	
	
	@Override
	public int updateInfo(MemberDTO inputMember, String[] memberAddress) {

		if(!inputMember.getMemberAddress().equals(",,")) {
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
			
			inputMember.setMemberAddress(null);
		}
		
	    // 닉네임, 전화번호, 소개글 수정
	    int result = mapper.updateInfo(inputMember);
	    
	    if (result == 0) return 0;
	    
	    int memberNo = inputMember.getMemberNo();

	    // 기존 취미 전부 삭제
	    mapper.deleteMemberHobby(memberNo);

	    // 새로 선택한 취미 등록
	    List<String> hobbyCodeList = inputMember.getHobbyCode();

	    if (hobbyCodeList != null && !hobbyCodeList.isEmpty()) {
	        
	    		for (String hobbyCode : hobbyCodeList) {
	        
	    			mapper.insertMemberHobby(memberNo, hobbyCode);
	        }
	    }
		
		return result;
	}
	
	
	@Override
	public int changePw(String newPw, String currentPw, MemberDTO loginMember) {

		String storePw = mapper.checkPw(loginMember.getMemberNo());
		
		if(!bcrypt.matches(currentPw,storePw)) return 0;
		
		String encPw = bcrypt.encode(newPw);
		loginMember.setMemberPw(encPw);
		
		return mapper.changePw(loginMember);
	}


	@Override
	public int profile(MultipartFile profileImg, MemberDTO loginMember) throws Exception {
		
		String updatePath = null;
		
		String rename = null;
		
		if(!profileImg.isEmpty()) {
			
			rename = Utility.fileRename(profileImg.getOriginalFilename());
			
			updatePath = profileWebPath;
			
		}
		
		MemberDTO member = MemberDTO.builder()
				.memberNo(loginMember.getMemberNo())
				.profilePath(updatePath)
				.profileOriginalName(profileImg.getOriginalFilename())
				.profileRename(rename)
				.build();
		
		int result = mapper.profile(member);
		
		if(result > 0) {
			
			if(!profileImg.isEmpty()) {
				
				profileImg.transferTo(new File(profileFolderPath + rename));
				
			}
			
			loginMember.setProfileOriginalName(profileImg.getOriginalFilename());
			loginMember.setProfilePath(updatePath);
			loginMember.setProfileRename(rename);
			
			// 합쳐진 경로 -> 세션에서 바로 쓸 수 있도록 추가(가상 필드)
			loginMember.setProfileFullPath(updatePath + rename);
			
		}
		return result;
	}
	
}










