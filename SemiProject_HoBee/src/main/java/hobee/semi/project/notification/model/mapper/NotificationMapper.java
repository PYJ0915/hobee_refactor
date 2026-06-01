package hobee.semi.project.notification.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hobee.semi.project.notification.model.dto.Notification;

@Mapper
public interface NotificationMapper {

	// 알림 생성
    int insertNotification(Notification notification);

    // 읽지 않은 알림 수 조회
    int getUnreadCount(int receiverNo);

    // 알림 목록 조회 (최근 20개)
    List<Notification> selectNotifications(int receiverNo);

    // 알림 읽음 처리 (단건)
    int readNotification(int notiNo);

    // 알림 전체 읽음 처리
    int readAllNotifications(int receiverNo);
    
    // 알림 개별 삭제
    int deleteNotification(int notiNo);
    
    // 알림 전체 삭제
    int deleteAllNotifications(int receiverNo);

}
