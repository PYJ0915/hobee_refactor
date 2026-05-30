package hobee.semi.project.chat.model.service;

import java.util.List;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;
import hobee.semi.project.member.model.dto.MemberDTO;

public interface ChatService {

	public ChatRoom getOrCreateDirectRoom(int memberNoA, int memberNoB);
	
	public List<ChatMessage> getMessages(int roomId);
	
	public void saveMessage(ChatMessage message);
	
	List<ChatRoomSummary> getMyRooms(int memberNo);
	
	ChatRoom createGroupRoom(String roomName, List<Integer> memberNos);
	
	boolean joinGroupRoom(int roomNo, int memberNo);
	
	int getUnreadChatCount(int memberNo);
	
	void markMessagesAsRead(int roomNo, int memberNo);
	
	void leaveRoom(int roomNo, int memberNo);
	
	List<MemberDTO> getRoomMembers(int roomNo);
	
	boolean inviteMember(int roomNo, int memberNo);
	
	void updateRoomName(int roomNo, String roomName);

	String generateGroupRoomName(int roomNo, int creatorMemberNo);
	
	void updateRoomImg(int roomNo, String roomImg);
	
}
