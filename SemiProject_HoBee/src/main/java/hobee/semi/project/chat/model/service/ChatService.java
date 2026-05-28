package hobee.semi.project.chat.model.service;

import java.util.List;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;

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
	
}
