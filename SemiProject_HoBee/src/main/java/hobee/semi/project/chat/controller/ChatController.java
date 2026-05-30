package hobee.semi.project.chat.controller;

import java.io.File;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;
import hobee.semi.project.chat.model.service.ChatService;
import hobee.semi.project.common.util.Utility;
import hobee.semi.project.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService service;
	
	@Value("${my.chat.folder-path}")
	private String chatFolderPath;

	@Value("${my.chat.web-path}")
	private String chatWebPath;

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
	public List<ChatRoomSummary> getMyRooms(@SessionAttribute("loginMember") MemberDTO loginMember) {
		return service.getMyRooms(loginMember.getMemberNo());
	}

	// 단체 채팅방 생성
	@ResponseBody
	@PostMapping("group")
	public Map<String, Object> createGroupRoom(@RequestBody Map<String, Object> body,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		String roomName = (String) body.get("roomName");

		@SuppressWarnings("unchecked")
		List<Integer> memberNos = (List<Integer>) body.get("memberNos");

		// 방장 본인도 포함
		if (!memberNos.contains(loginMember.getMemberNo())) {
			memberNos.add(0, loginMember.getMemberNo());
		}

		// roomName이 비어있으면 자동 생성 (참여자 INSERT 후 닉네임 조회)
		ChatRoom room = service.createGroupRoom(roomName, memberNos);

		if (roomName == null || roomName.isBlank()) {
			String autoName = service.generateGroupRoomName(room.getRoomNo(), loginMember.getMemberNo());
			service.updateRoomName(room.getRoomNo(), autoName);
			room.setRoomName(autoName);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("roomId", room.getRoomNo());
		result.put("roomName", room.getRoomName());
		return result;
	}

	// 단체 채팅방 참여
	@ResponseBody
	@PostMapping("group/{roomNo}/join")
	public Map<String, Object> joinGroupRoom(@PathVariable("roomNo") int roomNo,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		boolean joined = service.joinGroupRoom(roomNo, loginMember.getMemberNo());
		Map<String, Object> result = new HashMap<>();
		result.put("success", joined);
		return result;
	}

	// 안 읽은 채팅 수
	@ResponseBody
	@GetMapping("unreadCount")
	public int getUnreadChatCount(@SessionAttribute("loginMember") MemberDTO loginMember) {
		return service.getUnreadChatCount(loginMember.getMemberNo());
	}

	// 읽음 처리
	@ResponseBody
	@PostMapping("room/{roomId}/read")
	public void markAsRead(@PathVariable("roomId") int roomId, @SessionAttribute("loginMember") MemberDTO loginMember) {
		service.markMessagesAsRead(roomId, loginMember.getMemberNo());
	}

	// WebSocket: 단체 채팅 메시지
	@MessageMapping("chat/room/{roomId}")
	@SendTo("/topic/chat/room/{roomId}")
	public ChatMessage sendGroupMessage(@DestinationVariable("roomId") String roomId, ChatMessage message) {

		// TALK 타입만 저장 (TYPING, STOP_TYPING 혹시라도 여기로 오면 저장 방지)
		if (message.getType() == ChatMessage.MessageType.TALK) {
			message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			service.saveMessage(message);
		}

		return message;
	}

	@ResponseBody
	@PostMapping("room/{roomId}/leave")
	public void leaveRoom(@PathVariable("roomId") int roomId, @SessionAttribute("loginMember") MemberDTO loginMember) {

		service.leaveRoom(roomId, loginMember.getMemberNo());

		// 시스템 메시지 생성 및 저장
		ChatMessage sysMsg = new ChatMessage();
		sysMsg.setRoomId(roomId);
		sysMsg.setSenderNo(0); // 시스템 메시지는 0으로 구분
		sysMsg.setSender("SYSTEM");
		sysMsg.setContent(loginMember.getMemberNickname() + " 회원님이 채팅방을 나가셨습니다.");
		sysMsg.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		sysMsg.setType(ChatMessage.MessageType.LEAVE);

		service.saveMessage(sysMsg);

		// 같은 방 사람들에게 브로드캐스트
		messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, sysMsg);
	}

	// 타이핑 중 표시
	// DB 저장 없이 해당 방으로 브로드캐스트만 수행
	@MessageMapping("chat/typing")
	public void handleTyping(ChatMessage message) {
		messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
	}

	// 채팅방 참여자 목록 조회
	@ResponseBody
	@GetMapping("room/{roomId}/members")
	public List<MemberDTO> getRoomMembers(@PathVariable("roomId") int roomId) {
		return service.getRoomMembers(roomId);
	}

	// 채팅방 회원 초대
	@ResponseBody
	@PostMapping("room/{roomId}/invite")
	public Map<String, Object> inviteMember(@PathVariable("roomId") int roomId, @RequestBody Map<String, Object> body,
			@SessionAttribute("loginMember") MemberDTO loginMember) {

		int targetNo = Integer.parseInt(body.get("memberNo").toString());
		boolean invited = service.inviteMember(roomId, targetNo);

		Map<String, Object> result = new HashMap<>();
		result.put("success", invited);

		if (invited) {
			// 초대 시스템 메시지 브로드캐스트
			ChatMessage sysMsg = new ChatMessage();
			sysMsg.setRoomId(roomId);
			sysMsg.setSenderNo(0);
			sysMsg.setSender("SYSTEM");
			sysMsg.setType(ChatMessage.MessageType.SYSTEM);

			// 초대된 회원 닉네임 조회해서 메시지 생성
			// 간단히 body에서 닉네임 같이 받는 방식으로 처리
			String nickname = (String) body.getOrDefault("nickname", "");
			sysMsg.setContent(loginMember.getMemberNickname() + "님이 " + nickname + "님을 초대했습니다.");
			sysMsg.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

			service.saveMessage(sysMsg);
			messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, sysMsg);

			// DIRECT → GROUP 전환 시 자동 이름 갱신
			// (초대로 인해 참여자가 3명 이상이 되면 이름 자동 업데이트)
			String autoName = service.generateGroupRoomName(roomId, loginMember.getMemberNo());
			service.updateRoomName(roomId, autoName);
			result.put("roomName", autoName);
		}

		return result;
	}
	
	// 채팅방 이름 수정
	@ResponseBody
	@PostMapping("room/{roomId}/name")
	public Map<String, Object> updateRoomName(
	        @PathVariable("roomId") int roomId,
	        @RequestBody Map<String, Object> body,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {

	    String roomName = ((String) body.get("roomName")).trim();
	    Map<String, Object> result = new HashMap<>();

	    if (roomName.isEmpty()) {
	        result.put("success", false);
	        result.put("message", "방 이름을 입력해주세요.");
	        return result;
	    }
	    if (roomName.length() > 30) {
	        result.put("success", false);
	        result.put("message", "방 이름은 30자 이하로 입력해주세요.");
	        return result;
	    }

	    service.updateRoomName(roomId, roomName);

	    // 방 이름 변경 시스템 메시지 브로드캐스트
	    ChatMessage sysMsg = new ChatMessage();
	    sysMsg.setRoomId(roomId);
	    sysMsg.setSenderNo(0);
	    sysMsg.setSender("SYSTEM");
	    sysMsg.setContent(loginMember.getMemberNickname()
	        + "님이 채팅방 이름을 '" + roomName + "'(으)로 변경했습니다.");
	    sysMsg.setSendTime(LocalDateTime.now()
	        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	    sysMsg.setType(ChatMessage.MessageType.SYSTEM);

	    service.saveMessage(sysMsg);
	    messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, sysMsg);

	    result.put("success", true);
	    result.put("roomName", roomName);
	    return result;
	}
	
	// 채팅방 이미지 업로드
	@ResponseBody
	@PostMapping("room/{roomId}/img")
	public Map<String, Object> updateRoomImg(
	        @PathVariable("roomId") int roomId,
	        @RequestParam("roomImg") MultipartFile imgFile,
	        @SessionAttribute("loginMember") MemberDTO loginMember) {

	    Map<String, Object> result = new HashMap<>();

	    if (imgFile == null || imgFile.isEmpty()) {
	        result.put("success", false);
	        result.put("message", "이미지 파일을 선택해주세요.");
	        return result;
	    }

	    try {
	        // 기존 Utility.fileRename() 방식 그대로 사용
	        String rename = Utility.fileRename(imgFile.getOriginalFilename());

	        // 저장 폴더 없으면 생성
	        File folder = new File(chatFolderPath);
	        if (!folder.exists()) folder.mkdirs();

	        // 파일 저장
	        imgFile.transferTo(new File(chatFolderPath + rename));

	        // DB에 웹 경로 저장
	        String imgPath = chatWebPath + rename;
	        service.updateRoomImg(roomId, imgPath);

	        // 이름 변경 시스템 메시지
	        ChatMessage sysMsg = new ChatMessage();
	        sysMsg.setRoomId(roomId);
	        sysMsg.setSenderNo(0);
	        sysMsg.setSender("SYSTEM");
	        sysMsg.setContent(loginMember.getMemberNickname() + "님이 채팅방 사진을 변경했습니다.");
	        sysMsg.setSendTime(LocalDateTime.now()
	            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        sysMsg.setType(ChatMessage.MessageType.SYSTEM);
	        service.saveMessage(sysMsg);
	        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, sysMsg);

	        result.put("success", true);
	        result.put("roomImg", imgPath);

	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", "이미지 업로드에 실패했습니다.");
	        e.printStackTrace();
	    }

	    return result;
	}

}
