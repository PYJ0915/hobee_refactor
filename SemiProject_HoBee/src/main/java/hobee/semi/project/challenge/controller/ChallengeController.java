package hobee.semi.project.challenge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hobee.semi.project.board.model.service.EditBoardService;
import hobee.semi.project.challenge.model.dto.Cert;
import hobee.semi.project.challenge.model.dto.Challenge;
import hobee.semi.project.challenge.model.service.ChallengeService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("challenge")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

	private final ChallengeService service;

	private final EditBoardService editBoardService;

	// 챌린지 목록 페이지
	@GetMapping("list")
	public String challengeList(@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "type", required = false) String type,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember, Model model) {

		List<Challenge> list = service.getChallengeList(status, type);
		model.addAttribute("challengeList", list);
		model.addAttribute("currentStatus", status);
		model.addAttribute("currentType", type);
		return "challenge/challengeList";
	}

	// 챌린지 상세 페이지
	@GetMapping("detail/{challengeNo}")
	public String challengeDetail(@PathVariable("challengeNo") int challengeNo,
			@SessionAttribute(value = "loginMember", required = false) MemberDTO loginMember, Model model,
			RedirectAttributes ra) {

		int memberNo = loginMember != null ? loginMember.getMemberNo() : 0;
		Challenge challenge = service.getChallenge(challengeNo, memberNo);

		if (challenge == null) {
			ra.addFlashAttribute("message", "존재하지 않는 챌린지입니다.");
			return "redirect:/challenge/list";
		}

	    List<Cert> allCertList = service.getAllCertList(challengeNo);
	    model.addAttribute("allCertList", allCertList);

		model.addAttribute("challenge", challenge);
		return "challenge/challengeDetail";
	}

	// 챌린지 생성 페이지
	@GetMapping("create")
	public String createPage() {
		return "challenge/challengeCreate";
	}

	// 챌린지 생성 처리
	@PostMapping("create")
	public String create(Challenge challenge, @SessionAttribute("loginMember") MemberDTO loginMember,
			RedirectAttributes ra) {

		challenge.setMemberNo(loginMember.getMemberNo());
		service.createChallenge(challenge);

		ra.addFlashAttribute("message", "챌린지가 등록되었습니다!");
		return "redirect:/challenge/list";
	}

	// 참여 신청 (REST)
	@ResponseBody
	@PostMapping("join/{challengeNo}")
	public Map<String, Object> join(@PathVariable("challengeNo") int challengeNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.joinChallenge(challengeNo, loginMember.getMemberNo());
	}

	// 인증 등록 (REST)
	@ResponseBody
	@PostMapping("certify/{challengeNo}")
	public Map<String, Object> certify(@PathVariable("challengeNo") int challengeNo,
			@RequestParam("certTitle") String certTitle, @RequestParam("certContent") String certContent,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		Cert cert = Cert.builder().certTitle(certTitle).certContent(certContent).build();

		return service.certify(challengeNo, loginMember.getMemberNo(), cert);
	}

	// 내 챌린지 목록 (마이페이지용)
	@ResponseBody
	@GetMapping("my")
	public List<Challenge> myChallenges(@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.getMyChallenges(loginMember.getMemberNo());
	}

	// 특정 챌린지의 특정 회원 인증 내역 조회
	@ResponseBody
	@GetMapping("certList")
	public List<Cert> getCertList(@RequestParam("challengeNo") int challengeNo,
			@RequestParam("memberNo") int memberNo) {
		return service.getCertList(challengeNo, memberNo);
	}

	@ResponseBody
	@PostMapping("imageUpload")
	public String imageUpload(@RequestParam("file") MultipartFile file) throws Exception {
		return service.imageUpload(file);
	}

}