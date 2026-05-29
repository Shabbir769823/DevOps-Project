package com.devops.employee.service;

import com.devops.employee.model.Notification;
import com.devops.employee.model.User;
import com.devops.employee.repository.NotificationRepository;
import com.devops.employee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Notification> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByRecipientAndReadStatusOrderByCreatedAtDesc(user, false);
    }

    @Override
    public long getUnreadCountForUser(User user) {
        return notificationRepository.countByRecipientAndReadStatus(user, false);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void sendNotificationToAdmins(String message) {
        // Find all users who have the admin role
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName())))
                .collect(Collectors.toList());

        for (User admin : admins) {
            Notification notification = new Notification(message, admin);
            notificationRepository.save(notification);
        }
    }
}
