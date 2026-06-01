package hobee.semi.project.common.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hobee.semi.project.challenge.model.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {

    private final ChallengeService service;

    // 매일 자정에 만료된 챌린지 자동 마감
    @Scheduled(cron = "0 0 0 * * *")
    public void closeExpiredChallenges() {
        log.info("챌린지 자동 마감 스케줄러 실행");
        service.closeExpiredChallenges();
    }
}