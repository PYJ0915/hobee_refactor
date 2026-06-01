package hobee.semi.project.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomSummary {
	
    private int    roomNo;
    private String roomType;       // DIRECT / GROUP
    private String roomName;       // 단체: 방 이름 / 1:1: 상대방 닉네임
    private String roomProfileImg; // 단체: 방 이미지 / 1:1: 상대방 프로필
    private String lastMessage;    // 마지막 메시지 내용
    private String lastMessageTime; // 마지막 메시지 시간
    private int    unreadCount;    // 안 읽은 메시지 수
    private int    memberCount;    // 참여자 수 (단체용)
    
}