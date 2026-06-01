package hobee.semi.project.gathering.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import hobee.semi.project.gathering.model.service.GatheringService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("gathering")
@RequiredArgsConstructor
@Slf4j
public class GatheringController {

	private final GatheringService service;

	// 참여 신청
	@PostMapping("join/{gatheringNo}")
	public Map<String, Object> join(@PathVariable("gatheringNo") int gatheringNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.joinGathering(gatheringNo, loginMember.getMemberNo());
	}

	// 참여 취소
	@PostMapping("cancel/{gatheringNo}")
	public Map<String, Object> cancel(@PathVariable("gatheringNo") int gatheringNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.cancelGathering(gatheringNo, loginMember.getMemberNo());
	}

	// 모임 확정 (작성자만)
	@PostMapping("confirm/{gatheringNo}")
	public Map<String, Object> confirm(@PathVariable("gatheringNo") int gatheringNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		Map<String, Object> result = new HashMap<>();

	    try {
	        int roomNo = service.confirmGathering(gatheringNo, loginMember.getMemberNo());
	        result.put("success", true);
	        result.put("roomNo", roomNo);
	    } catch (IllegalStateException e) {
	        result.put("success", false);
	        result.put("message", e.getMessage()); 
	    }

	    return result;
	}

	// 수락
	@PostMapping("approve/{gatheringNo}/{targetMemberNo}")
	public Map<String, Object> approve(@PathVariable("gatheringNo") int gatheringNo,
			@PathVariable("targetMemberNo") int targetMemberNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.approveJoin(gatheringNo, targetMemberNo, loginMember.getMemberNo());
	}

	// 거절
	@PostMapping("reject/{gatheringNo}/{targetMemberNo}")
	public Map<String, Object> reject(@PathVariable("gatheringNo") int gatheringNo,
			@PathVariable("targetMemberNo") int targetMemberNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		return service.rejectJoin(gatheringNo, targetMemberNo, loginMember.getMemberNo());
	}
	
	@GetMapping("getBoardNo/{gatheringNo}")
	public int getBoardNo(@PathVariable("gatheringNo") int gatheringNo) {
	    return service.getBoardNo(gatheringNo);
	}
}