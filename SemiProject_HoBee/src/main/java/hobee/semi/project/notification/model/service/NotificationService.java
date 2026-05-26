package hobee.semi.project.notification.model.service;

import java.util.List;

import hobee.semi.project.notification.model.dto.Notification;

public interface NotificationService {

	int getUnreadCount(int memberNo);

	List<Notification> getNotifications(int memberNo);

	int readNotification(int notiNo);

	int readAllNotifications(int memberNo);
	
	int deleteNotification(int notiNo);
	
	int deleteAllNotifications(int receiverNo);

}
