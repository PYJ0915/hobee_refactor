package hobee.semi.project.follow.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.follow.model.dto.Follow;

@Mapper
public interface FollowMapper {
	
	// 팔로우 추가
    int insertFollow(Follow follow);

    // 언팔로우
    int deleteFollow(Follow follow);

    // 팔로우 여부 확인
    int checkFollow(Follow follow);

    // 팔로워 수 (나를 팔로우하는 사람 수)
    int getFollowerCount(int memberNo);

    // 팔로잉 수 (내가 팔로우하는 사람 수)
    int getFollowingCount(int memberNo);
    
    // 팔로워 목록 (나를 팔로우하는 사람들)
    List<Follow> getFollowerList(int memberNo);
    
    // 팔로잉 목록 (내가 팔로우하는 사람들)
    List<Follow> getFollowingList(int memberNo);
    
    // 팔로워 memberNo 목록
    List<Integer> getFollowerNoList(int memberNo); 

}
