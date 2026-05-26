package hobee.semi.project.follow.model.service;

import java.util.List;
import java.util.Map;

import hobee.semi.project.follow.model.dto.Follow;

public interface FollowService {
	
	Map<String, Object> toggleFollow(int followerNo, int followingNo);
	
    boolean isFollowing(int followerNo, int followingNo);
    
    int getFollowerCount(int memberNo);
    
    int getFollowingCount(int memberNo);
    
    List<Follow> getFollowerList(int memberNo);
    
    List<Follow> getFollowingList(int memberNo);
    
}
