package hobee.semi.project.follow.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.follow.model.dto.Follow;
import hobee.semi.project.follow.model.mapper.FollowMapper;
import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService{

	private final FollowMapper followMapper;
    private final NotificationMapper notificationMapper;

    @Override
    public Map<String, Object> toggleFollow(int followerNo, int followingNo) {

        Follow follow = Follow.builder()
            .followerNo(followerNo)
            .followingNo(followingNo)
            .build();

        Map<String, Object> result = new HashMap<>();

        int isFollowing = followMapper.checkFollow(follow);

        if (isFollowing > 0) {
            // 이미 팔로우 중 → 언팔로우
            followMapper.deleteFollow(follow);
            result.put("isFollowing", false);
            log.debug("언팔로우 - follower: {}, following: {}", followerNo, followingNo);

        } else {
            // 팔로우 안 함 → 팔로우
            followMapper.insertFollow(follow);
            result.put("isFollowing", true);
            log.debug("팔로우 - follower: {}, following: {}", followerNo, followingNo);

            // 알림 생성
            Notification noti = Notification.builder()
                .receiverNo(followingNo)
                .senderNo(followerNo)
                .notiType("FOLLOW")
                .notiTargetNo(followerNo)
                .notiMessage("님이 팔로우했습니다.")
                .build();
            notificationMapper.insertNotification(noti);
        }

        // 최신 팔로워 수 반환
        result.put("followerCount", followMapper.getFollowerCount(followingNo));

        return result;
    }

    @Override
    public boolean isFollowing(int followerNo, int followingNo) {
        Follow follow = Follow.builder()
            .followerNo(followerNo)
            .followingNo(followingNo)
            .build();
        return followMapper.checkFollow(follow) > 0;
    }

    @Override
    public int getFollowerCount(int memberNo) {
        return followMapper.getFollowerCount(memberNo);
    }

    @Override
    public int getFollowingCount(int memberNo) {
        return followMapper.getFollowingCount(memberNo);
    }
    
    @Override
    public List<Follow> getFollowerList(int memberNo) {
        return followMapper.getFollowerList(memberNo);
    }

    @Override
    public List<Follow> getFollowingList(int memberNo) {
        return followMapper.getFollowingList(memberNo);
    }
	
}
