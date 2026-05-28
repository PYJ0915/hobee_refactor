package hobee.semi.project.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
	
    private int    roomNo;
    private String roomType;   // "DIRECT"
    private String roomName;
    private String createDate;
    
}
