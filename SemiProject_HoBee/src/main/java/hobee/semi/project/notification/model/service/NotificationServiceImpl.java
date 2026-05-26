package hobee.semi.project.notification.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobee.semi.project.notification.model.dto.Notification;
import hobee.semi.project.notification.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
	
	private final NotificationMapper mapper;

    @Override
    public int getUnreadCount(int receiverNo) {
        return mapper.getUnreadCount(receiverNo);
    }

    @Override
    public List<Notification> getNotifications(int receiverNo) {
        return mapper.selectNotifications(receiverNo);
    }

    @Override
    public int readNotification(int notiNo) {
        return mapper.readNotification(notiNo);
    }

    @Override
    public int readAllNotifications(int receiverNo) {
        return mapper.readAllNotifications(receiverNo);
    }
    
    @Override
    public int deleteNotification(int notiNo) {
        return mapper.deleteNotification(notiNo);
    }

    @Override
    public int deleteAllNotifications(int receiverNo) {
        return mapper.deleteAllNotifications(receiverNo);
    }

}
