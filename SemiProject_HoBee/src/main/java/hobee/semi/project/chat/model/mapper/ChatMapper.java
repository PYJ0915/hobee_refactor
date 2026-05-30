package hobee.semi.project.chat.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;
import hobee.semi.project.member.model.dto.MemberDTO;

@Mapper
public interface ChatMapper {

	void insertRoom(ChatRoom room);

	void insertRoomMember(@Param("roomNo") int roomNo, @Param("memberNo") int memberNo);

	List<ChatMessage> selectMessages(int roomId);

	void insertMessage(ChatMessage message);

	ChatRoom findDirectRoom(@Param("memberNoA") int memberNoA, @Param("memberNoB") int memberNoB);

	List<ChatRoomSummary> selectMyRooms(int memberNo);

	int checkRoomMember(@Param("roomNo") int roomNo, @Param("memberNo") int memberNo);

	int getUnreadChatCount(int memberNo);

	void markMessagesAsRead(@Param("roomNo") int roomNo, @Param("memberNo") int memberNo);

	void leaveRoom(@Param("roomNo") int roomNo, @Param("memberNo") int memberNo);

	// 채팅방 현재 참여자 목록 조회
	List<MemberDTO> selectRoomMembers(int roomNo);

	void updateRoomName(@Param("roomNo") int roomNo, @Param("roomName") String roomName);

	List<String> selectRoomMemberNicknames(int roomNo);
	
	void updateRoomImg(@Param("roomNo") int roomNo, @Param("roomImg") String roomImg);

}
