package hobee.semi.project.notification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

	private final NotificationService service;
	
	// 읽지 않은 알람 수
	@GetMapping("unreadCount")
	public int getUnreadCount(@SessionAttribute("loginMember") MemberDTO loginMember) {
		return service.getUnreadCount(loginMember.getMemberNo());
	}
	
	// 알림 목록
    @GetMapping("list")
    public List<Notification> getNotifications(
            @SessionAttribute("loginMember") MemberDTO loginMember) {
        return service.getNotifications(loginMember.getMemberNo());
    }

    // 단건 읽음 처리
    @PutMapping("read/{notiNo}")
    public int readNotification(@PathVariable("notiNo") int notiNo) {
        return service.readNotification(notiNo);
    }

    // 전체 읽음 처리
    @PutMapping("readAll")
    public int readAll(
            @SessionAttribute("loginMember") MemberDTO loginMember) {
        return service.readAllNotifications(loginMember.getMemberNo());
    }
    
    // 단건 삭제
    @DeleteMapping("delete/{notiNo}")
    public int deleteNotification(@PathVariable("notiNo") int notiNo) {
        return service.deleteNotification(notiNo);
    }

    // 전체 삭제
    @DeleteMapping("deleteAll")
    public int deleteAll(@SessionAttribute("loginMember") MemberDTO loginMember) {
        return service.deleteAllNotifications(loginMember.getMemberNo());
    }
	
}
