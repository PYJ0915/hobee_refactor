package hobee.semi.project.follow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import hobee.semi.project.follow.model.dto.Follow;
import hobee.semi.project.follow.model.service.FollowService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("follow")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

	 private final FollowService service;

	    // 팔로우 / 언팔로우 토글
	    @PostMapping("toggle")
	    public Map<String, Object> toggleFollow(
	            @RequestBody Map<String, Integer> body,
	            @SessionAttribute("loginMember") MemberDTO loginMember) {

	        int followerNo  = loginMember.getMemberNo();
	        int followingNo = body.get("followingNo");

	        return service.toggleFollow(followerNo, followingNo);
	    }
	    
	    @GetMapping("followers/{memberNo}")
	    public List<Follow> getFollowers(@PathVariable("memberNo") int memberNo) {
	        return service.getFollowerList(memberNo);
	    }

	    @GetMapping("followings/{memberNo}")
	    public List<Follow> getFollowings(@PathVariable("memberNo") int memberNo) {
	        return service.getFollowingList(memberNo);
	    }
	    
}
