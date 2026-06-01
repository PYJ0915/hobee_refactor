package hobee.semi.project.myPage.model.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.common.util.Utility;
import hobee.semi.project.findHobby.model.dto.Hobby;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
@PropertySource("classpath:/config.properties")
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

	private final MyPageMapper mapper;

	private final BCryptPasswordEncoder bcrypt;

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

		if (!inputMember.getMemberAddress().equals(",,")) {

			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);

		} else {

			inputMember.setMemberAddress(null);
		}

		// 닉네임, 전화번호, 소개글 수정
		int result = mapper.updateInfo(inputMember);

		if (result == 0)
			return 0;

		int memberNo = inputMember.getMemberNo();

		// 기존 취미 전부 삭제
		mapper.deleteMemberHobby(memberNo);

		// 새로 선택한 취미 등록
		List<String> hobbyCodeList = inputMember.getHobbyCode();

		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);

		if (hobbyCodeList != null && !hobbyCodeList.isEmpty()) {

			for (String hobbyCode : hobbyCodeList) {

				map.put("hobbyCode", hobbyCode);

				mapper.insertMemberHobby(map);
			}
		}

		return result;
	}

	@Override
	public int checkNickname(String memberNickname, int memberNo) {

		Map<String, Object> map = new HashMap<>();
		map.put("memberNickname", memberNickname);
		map.put("memberNo", memberNo);

		return mapper.checkNickname(map);
	}

	@Override
	public int changePw(String newPw, String currentPw, MemberDTO loginMember) {

		String storePw = mapper.checkPw(loginMember.getMemberNo());

		if (!bcrypt.matches(currentPw, storePw))
			return 0;

		String encPw = bcrypt.encode(newPw);
		loginMember.setMemberPw(encPw);

		return mapper.changePw(loginMember);
	}

	@Override
	public List<Board> selectBoardList(int memberNo) {

		return mapper.selectBoardList(memberNo);
	}

	@Override
	public int profile(MultipartFile profileImg, boolean isDefault, MemberDTO loginMember) throws Exception {

		// ===================== 기본 이미지 변경 =====================
		if (isDefault) {

			MemberDTO member = MemberDTO.builder().memberNo(loginMember.getMemberNo()).profileImg(null)
					.profilePath(null).profileOriginalName(null).profileRename(null).profileFullPath(null).build();

			int updateProfile = mapper.updateProfile(member);

			if (updateProfile > 0) {

				if (loginMember.getProfileImg() != null && loginMember.getProfileRename() != null) {

					mapper.deleteProfile(member);

					File oldFile = new File(profileFolderPath + loginMember.getProfileRename());

					if (oldFile.exists()) {
						oldFile.delete();
					}
				}

				loginMember.setProfileImg(null);
				loginMember.setProfilePath(null);
				loginMember.setProfileOriginalName(null);
				loginMember.setProfileRename(null);

				log.info("기본 이미지로 변경 완료 - memberNo: {}", loginMember.getMemberNo());
			}

			return updateProfile;
		}

		// ===================== 프로필 업로드 =====================

		if (profileImg.isEmpty()) {
			return 0;
		}

		log.info("service profileImg : {}", profileImg.getOriginalFilename());

		String rename = Utility.fileRename(profileImg.getOriginalFilename());

		String profilePath = profileWebPath;
		String updatePath = profilePath + rename;

		String oldRename = loginMember.getProfileRename();

		MemberDTO member = MemberDTO.builder().memberNo(loginMember.getMemberNo()).profileImg(updatePath)
				.profilePath(profilePath).profileOriginalName(profileImg.getOriginalFilename()).profileRename(rename)
				.profileFullPath(updatePath).build();

		// PROFILE_IMG 테이블 저장
		int result = mapper.profile(member);

		if (result <= 0) {
			return 0;
		}

		log.info("프로필 테이블 저장 성공 : {}", member.getProfileImg());

		// ===================== 파일 저장 =====================

		String savePath = profileFolderPath + rename;

		try {

			if (Utility.isResizableImage(profileImg.getOriginalFilename())) {
				Utility.resizeProfile(profileImg, savePath);
			} else {
				profileImg.transferTo(new File(savePath));
			}
		} catch (Exception e) {

			log.error("프로필 이미지 파일 저장 실패 : {}", savePath, e);

			// @Transactional rollback 유도
			throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
		}

		// ===================== MEMBER 테이블 업데이트 =====================

		int updateProfile = mapper.updateProfile(member);

		if (updateProfile <= 0) {
			throw new RuntimeException("회원 프로필 정보 업데이트 실패");
		}

		log.info("MEMBER TABLE PROFILE_IMG UPDATE 성공");

		// ===================== 기존 파일 삭제 =====================

		if (oldRename != null) {

			File oldFile = new File(profileFolderPath + oldRename);

			if (oldFile.exists()) {

				oldFile.delete();

				log.info("기존 프로필 파일 삭제 완료 : {}", oldFile.getName());
			}
		}

		// ===================== Session 갱신 =====================

		loginMember.setProfileImg(updatePath);
		loginMember.setProfilePath(profilePath);
		loginMember.setProfileOriginalName(profileImg.getOriginalFilename());
		loginMember.setProfileRename(rename);

		return updateProfile;
	}

	// 회원 탈퇴
	@Override
	public int secession(String memberPw, int memberNo) {

		String encPw = mapper.checkPw(memberNo);

		if (!bcrypt.matches(memberPw, encPw)) {
			return 0;
		}

		return mapper.secession(memberNo);
	}
}
