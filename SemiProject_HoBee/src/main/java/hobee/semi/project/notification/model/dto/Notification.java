package hobee.semi.project.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	private int notiNo;
    private int receiverNo;
    private int senderNo;
    private String notiType;
    private int notiTargetNo;
    private String notiMessage;
    private String notiReadFl;
    private String notiDate;
    private String senderNickname; // 표시용
    private String senderProfileImg; // 표시용
	
}
