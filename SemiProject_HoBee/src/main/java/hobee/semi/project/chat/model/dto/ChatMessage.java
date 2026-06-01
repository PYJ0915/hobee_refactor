package hobee.semi.project.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

	public enum MessageType {
	    TALK,
	    ENTER,
	    LEAVE,
	    SYSTEM,
	    TYPING,
	    STOP_TYPING
	}

    private MessageType type;
    private int  roomId;
    private int     senderNo;    // 보낸 사람 회원번호
    private String  sender;      // 보낸 사람 닉네임 (Principal 식별자)
    private String  receiver;    // 받는 사람 닉네임 (convertAndSendToUser 식별자)
    private int     receiverNo;  // 받는 사람 회원번호
    private String  content;
    private String  sendTime;
    private String senderProfileImg;
    
}
