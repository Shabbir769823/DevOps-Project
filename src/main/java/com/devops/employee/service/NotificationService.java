package com.devops.employee.service;

import com.devops.employee.model.Notification;
import com.devops.employee.model.User;
import java.util.List;

public interface NotificationService {
    Notification saveNotification(Notification notification);
    List<Notification> getNotificationsForUser(User user);
    List<Notification> getUnreadNotificationsForUser(User user);
    long getUnreadCountForUser(User user);
    void markAsRead(Long notificationId);
    void sendNotificationToAdmins(String message);
}
