package hobee.semi.project.challenge.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.board.model.dto.Board;
import hobee.semi.project.challenge.model.dto.Cert;
import hobee.semi.project.challenge.model.dto.Challenge;
import hobee.semi.project.challenge.model.mapper.CertMapper;
import hobee.semi.project.challenge.model.mapper.ChallengeMapper;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeMapper mapper;
    private final CertMapper  certMapper;
    private final NotificationMapper notificationMapper;

    @Override
    public void createChallenge(Challenge challenge) {
        mapper.insertChallenge(challenge);
    }

    @Override
    public List<Challenge> getChallengeList(String status, String type) {
        return mapper.selectChallengeList(status, type);
    }

    @Override
    public Challenge getChallenge(int challengeNo, int memberNo) {
        Challenge c = mapper.selectChallenge(challengeNo, memberNo);
        if (c == null) return null;

        // isJoined: myCount가 null이 아니면 참여 중
        c.setJoined(c.getMyCount() > 0 || mapper.checkJoined(challengeNo, memberNo) > 0);
        return c;
    }

    @Override
    public Map<String, Object> joinChallenge(int challengeNo, int memberNo) {
        Map<String, Object> result = new HashMap<>();

        if (mapper.checkJoined(challengeNo, memberNo) > 0) {
            result.put("success", false);
            result.put("message", "이미 참여 중인 챌린지입니다.");
            return result;
        }

        mapper.joinChallenge(challengeNo, memberNo);
        result.put("success", true);
        result.put("message", "챌린지에 참여했습니다!");
        return result;
    }

    @Override
    public Map<String, Object> certify(int challengeNo, int memberNo,
    		Cert cert) {
        Map<String, Object> result = new HashMap<>();

        // 참여 여부 확인
        if (mapper.checkJoined(challengeNo, memberNo) == 0) {
            result.put("success", false);
            result.put("message", "챌린지에 먼저 참여해주세요.");
            return result;
        }

        // 인증 게시글 저장 (boardCode=5, challengeNo 연결)
        cert.setChallengeNo(challengeNo);
        cert.setMemberNo(memberNo);
        certMapper.insertCert(cert);

        // 인증 횟수 증가
        mapper.incrementCertCount(challengeNo, memberNo);

        // 목표 달성 체크
        int updated = mapper.completeChallenge(challengeNo, memberNo);

        if (updated > 0) {
            // 목표 달성 알림
            notificationMapper.insertNotification(
                Notification.builder()
                    .receiverNo(memberNo)
                    .senderNo(memberNo)     // 시스템 알림은 본인 → 본인
                    .notiType("CHALLENGE")
                    .notiTargetNo(challengeNo)
                    .notiMessage("챌린지 목표를 달성했습니다! 🎉")
                    .build()
            );
            result.put("complete", true);
        }

        result.put("success", true);
        result.put("message", "인증 완료!");
        return result;
    }

    @Override
    public List<Challenge> getMyChallenges(int memberNo) {
        return mapper.selectMyChallenges(memberNo);
    }

    @Override
    public void closeExpiredChallenges() {
        List<Challenge> expired = mapper.selectExpiredChallenges();
        expired.forEach(c -> {
            mapper.updateStatus(c.getChallengeNo(), "DONE");
            log.info("챌린지 자동 마감: challengeNo={}", c.getChallengeNo());
        });
    }
    
    @Override
    public List<Cert> getCertList(int challengeNo, int memberNo) {
        return certMapper.selectCertList(challengeNo, memberNo);
    }
}