package hobee.semi.project.gathering.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.chat.model.dto.ChatRoom;
import hobee.semi.project.chat.model.service.ChatService;
import hobee.semi.project.gathering.model.dto.Gathering;
import hobee.semi.project.gathering.model.mapper.GatheringMapper;
import hobee.semi.project.member.model.dto.MemberDTO;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class GatheringServiceImpl implements GatheringService {

    private final GatheringMapper mapper;
    private final ChatService     chatService;
    private final NotificationMapper notificationMapper;

    @Override
    public void createGathering(Gathering gathering, int memberNo) {
        mapper.insertGathering(gathering);
        mapper.joinApproved(gathering.getGatheringNo(), memberNo); // joinGathering → joinApproved, boardNo → memberNo
    }
    
    @Override
    public Gathering getGathering(int boardNo, int loginMemberNo) {
        Gathering gathering = mapper.selectGathering(boardNo);
        if (gathering == null) return null;

        // 신청 대기 목록
        gathering.setPendingList(
            mapper.selectPendingMembers(gathering.getGatheringNo())
        );

        // 확정 참여자 목록
        gathering.setApprovedList(
            mapper.selectApprovedMembers(gathering.getGatheringNo())
        );

        // 내 참여 여부
        if (loginMemberNo > 0) {
            int joined = mapper.checkJoined(gathering.getGatheringNo(), loginMemberNo);
            gathering.setJoined(joined > 0);
            
            String myStatus = mapper.selectMyJoinStatus(gathering.getGatheringNo(), loginMemberNo);
            gathering.setMyJoinStatus(myStatus); // null이면 미신청
        }

        return gathering;
    }

    @Override
    public Map<String, Object> joinGathering(int gatheringNo, int memberNo) {
        Map<String, Object> result = new HashMap<>();

        // 기존 참여 중 체크
        int alreadyJoined = mapper.checkJoined(gatheringNo, memberNo);
        if (alreadyJoined > 0) {
            result.put("success", false);
            result.put("message", "이미 참여 중입니다.");
            return result;
        }
        
        // 인원 초과 체크 추가
        Gathering gathering = mapper.selectGatheringByNo(gatheringNo);
        if (gathering.getCurrentMember() >= gathering.getMaxMember()) {
            result.put("success", false);
            result.put("message", "모집 인원이 꽉 찼습니다.");
            return result;
        }
        
        // 모임이 이미 마감됐는지 체크
        if ("CLOSED".equals(gathering.getGatheringStatus())) {
            result.put("success", false);
            result.put("message", "이미 마감된 모임입니다.");
            return result;
        }

        mapper.joinGathering(gatheringNo, memberNo); // PENDING으로만 등록

        result.put("success", true);
        result.put("message", "참여 신청 완료! 작성자의 수락을 기다려주세요.");
        return result;
    }

    @Override
    public Map<String, Object> cancelGathering(int gatheringNo, int memberNo) {
        Map<String, Object> result = new HashMap<>();

        // 취소 전에 APPROVED 상태인지 먼저 확인
        int isApproved = mapper.checkApproved(gatheringNo, memberNo);

        mapper.cancelGathering(gatheringNo, memberNo);

        // APPROVED였던 경우만 인원 감소
        if (isApproved > 0) {
            mapper.decrementMember(gatheringNo);
        }

        result.put("success", true);
        return result;
    }

    @Override
    public int confirmGathering(int gatheringNo, int loginMemberNo) {

        // APPROVED 멤버만 조회
        List<MemberDTO> members = mapper.selectApprovedMembers(gatheringNo);
        List<Integer> memberNos = members.stream()
            .map(MemberDTO::getMemberNo)
            .collect(Collectors.toList());

        // 작성자 포함
        if (!memberNos.contains(loginMemberNo)) {
            memberNos.add(0, loginMemberNo);
        }
        
        String boardTitle = mapper.selectBoardTitle(gatheringNo);

        ChatRoom room = chatService.createGroupRoom(boardTitle, memberNos);
        mapper.updateRoomNo(gatheringNo, room.getRoomNo());
        mapper.updateStatus(gatheringNo, "CLOSED");

        // APPROVED 참여자 전원 알림
        members.forEach(m -> {
            if (m.getMemberNo() == loginMemberNo) return;
            sendGatheringNoti(loginMemberNo, loginMemberNo, gatheringNo, "님의 모임이 확정되어 채팅방이 생성되었습니다.");
        });

        return room.getRoomNo();
    }
    
    @Override
    public Map<String, Object> approveJoin(int gatheringNo,
                                           int targetMemberNo,
                                           int loginMemberNo) {
        Map<String, Object> result = new HashMap<>();

        mapper.approveJoin(gatheringNo, targetMemberNo);
        mapper.incrementMember(gatheringNo);

        // 신청자에게 수락 알림
        sendGatheringNoti(targetMemberNo, loginMemberNo, gatheringNo, "님이 모임 참여 신청을 수락했습니다.");

        result.put("success", true);
        return result;
    }
    
    @Override
    public Map<String, Object> rejectJoin(int gatheringNo,
                                          int targetMemberNo,
                                          int loginMemberNo) {
        Map<String, Object> result = new HashMap<>();

        mapper.rejectJoin(gatheringNo, targetMemberNo);

        // 신청자에게 거절 알림
        sendGatheringNoti(targetMemberNo, loginMemberNo, gatheringNo, "님이 모임 참여 신청을 거절했습니다.");

        result.put("success", true);
        return result;
    }
    
    @Override
    public int getBoardNo(int gatheringNo) {
        return mapper.selectBoardNoByGatheringNo(gatheringNo);
    }
    
    @Override
    public void updateGathering(Gathering gathering) {
        mapper.updateGathering(gathering);
    }
    
    // Gathering 알림 메서드
    private void sendGatheringNoti(int receiverNo, int senderNo, int gatheringNo, String message) {
		Notification noti = Notification.builder()
		.receiverNo(receiverNo)
		.senderNo(senderNo)
		.notiType("GATHERING")
		.notiTargetNo(gatheringNo)
		.notiMessage(message)
		.build();
		notificationMapper.insertNotification(noti);
    }
}