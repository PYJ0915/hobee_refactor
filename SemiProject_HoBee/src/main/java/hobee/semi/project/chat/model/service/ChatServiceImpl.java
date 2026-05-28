package hobee.semi.project.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.chat.model.dto.ChatMessage;
import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.dto.ChatRoomSummary;
import hobee.semi.project.chat.model.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
	
	private final ChatMapper mapper;

    // 채팅방 조회 or 생성
    @Override
    public ChatRoom getOrCreateDirectRoom(int memberNoA, int memberNoB) {
        // 두 회원의 1:1 채팅방이 이미 있는지 확인
        ChatRoom room = mapper.findDirectRoom(memberNoA, memberNoB);
        if (room == null) {
            // 없으면 새로 생성
            room = new ChatRoom();
            room.setRoomType("DIRECT");
            mapper.insertRoom(room);                              // CHAT_ROOM INSERT
            mapper.insertRoomMember(room.getRoomNo(), memberNoA); // 참여자 A
            mapper.insertRoomMember(room.getRoomNo(), memberNoB); // 참여자 B
        }
        return room;
    }

    // 이전 메시지 조회 (최근 50개)
    @Override
    public List<ChatMessage> getMessages(int roomId) {
        return mapper.selectMessages(roomId);
    }

    // 메시지 저장
    @Override
    public void saveMessage(ChatMessage message) {
        mapper.insertMessage(message);
    }
    
    @Override
    public List<ChatRoomSummary> getMyRooms(int memberNo) {
        return mapper.selectMyRooms(memberNo);
    }

    @Override
    public ChatRoom createGroupRoom(String roomName, List<Integer> memberNos) {
        ChatRoom room = new ChatRoom();
        room.setRoomType("GROUP");
        room.setRoomName(roomName);
        mapper.insertRoom(room);

        for (int memberNo : memberNos) {
            mapper.insertRoomMember(room.getRoomNo(), memberNo);
        }
        return room;
    }

    @Override
    public boolean joinGroupRoom(int roomNo, int memberNo) {
        int exists = mapper.checkRoomMember(roomNo, memberNo);
        if (exists > 0) return false; // 이미 참여 중
        mapper.insertGroupRoomMember(roomNo, memberNo);
        return true;
    }

    @Override
    public int getUnreadChatCount(int memberNo) {
        return mapper.getUnreadChatCount(memberNo);
    }

    @Override
    public void markMessagesAsRead(int roomNo, int memberNo) {
        mapper.markMessagesAsRead(roomNo, memberNo);
    }
    
    @Override
    public void leaveRoom(int roomNo, int memberNo) {
        mapper.leaveRoom(roomNo, memberNo);
    }

}
