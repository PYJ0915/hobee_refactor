package hobee.semi.project.chat.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;
import hobee.semi.project.chat.model.service.ChatService;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService service;
	
	@GetMapping("")
    public String chatPage() {
        return "chat/chat";
    }

	// ------------------------------------------------
	// REST: 채팅방 조회 or 생성
	// POST /chat/direct/{targetMemberNo}
	// ------------------------------------------------
	@ResponseBody
	@PostMapping("direct/{targetMemberNo}")
	public Map<String, Object> getOrCreateDirectRoom(@PathVariable("targetMemberNo") int targetMemberNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		Map<String, Object> result = new HashMap<>();

		ChatRoom room = service.getOrCreateDirectRoom(loginMember.getMemberNo(), targetMemberNo);

		result.put("roomId", room.getRoomNo());
		return result;
	}

	// ------------------------------------------------
	// REST: 이전 메시지 조회
	// GET /chat/room/{roomId}/messages
	// ------------------------------------------------
	@ResponseBody
	@GetMapping("room/{roomId}/messages")
	public List<ChatMessage> getMessages(@PathVariable("roomId") int roomId) {
		return service.getMessages(roomId);
	}

	// ------------------------------------------------
	// WebSocket: 1:1 메시지 처리
	// 클라이언트 → /app/chat/direct
	// ------------------------------------------------
	@MessageMapping("chat/direct")
	public void sendDirectMessage(ChatMessage message, Principal principal) {

		message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		// DB 저장
		service.saveMessage(message);

		// 수신자에게 전송 → /user/{receiver}/queue/direct
		messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);

	}
	
	// 내 채팅방 목록 조회
	@ResponseBody
	@GetMapping("rooms")
	public List<ChatRoomSummary> getMyRooms(
	        @SessionAttribute("loginMember") MemberDTO loginMember) {
	    return service.getMyRooms(loginMember.getMemberNo());
	}

	// 단체 채팅방 생성
	@ResponseBody
	@PostMapping("group")
	public Map<String, Object> createGroupRoom(
	        @RequestBody Map<String, Object> body,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {

	    String roomName = (String) body.get("roomName");

	    @SuppressWarnings("unchecked")
	    List<Integer> memberNos = (List<Integer>) body.get("memberNos");

	    // 방장 본인도 포함
	    if (!memberNos.contains(loginMember.getMemberNo())) {
	        memberNos.add(0, loginMember.getMemberNo());
	    }

	    ChatRoom room = service.createGroupRoom(roomName, memberNos);

	    Map<String, Object> result = new HashMap<>();
	    result.put("roomId", room.getRoomNo());
	    return result;
	}

	// 단체 채팅방 참여
	@ResponseBody
	@PostMapping("group/{roomNo}/join")
	public Map<String, Object> joinGroupRoom(
	        @PathVariable("roomNo") int roomNo,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {

	    boolean joined = service.joinGroupRoom(roomNo, loginMember.getMemberNo());
	    Map<String, Object> result = new HashMap<>();
	    result.put("success", joined);
	    return result;
	}

	// 안 읽은 채팅 수
	@ResponseBody
	@GetMapping("unreadCount")
	public int getUnreadChatCount(
	        @SessionAttribute("loginMember") MemberDTO loginMember) {
	    return service.getUnreadChatCount(loginMember.getMemberNo());
	}

	// 읽음 처리
	@ResponseBody
	@PostMapping("room/{roomId}/read")
	public void markAsRead(
	        @PathVariable("roomId") int roomId,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {
	    service.markMessagesAsRead(roomId, loginMember.getMemberNo());
	}

	// WebSocket: 단체 채팅 메시지
	@MessageMapping("chat/room/{roomId}")
	@SendTo("/topic/chat/room/{roomId}")
	public ChatMessage sendGroupMessage(
			@DestinationVariable("roomId") String roomId,
	        ChatMessage message) {

	    message.setSendTime(
	        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	    );
	    service.saveMessage(message);
	    return message;
	}
	
	@ResponseBody
	@PostMapping("room/{roomId}/leave")
	public void leaveRoom(
	        @PathVariable("roomId") int roomId,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {

	    service.leaveRoom(roomId, loginMember.getMemberNo());

	    // 시스템 메시지 생성 및 저장
	    ChatMessage sysMsg = new ChatMessage();
	    sysMsg.setRoomId(roomId);
	    sysMsg.setSenderNo(0); // 시스템 메시지는 0으로 구분
	    sysMsg.setSender("SYSTEM");
	    sysMsg.setContent(loginMember.getMemberNickname() + " 회원님이 채팅방을 나가셨습니다.");
	    sysMsg.setSendTime(LocalDateTime.now()
	        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    sysMsg.setType(ChatMessage.MessageType.LEAVE);

	    service.saveMessage(sysMsg);

	    // 같은 방 사람들에게 브로드캐스트
	    messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, sysMsg);
	}

}
