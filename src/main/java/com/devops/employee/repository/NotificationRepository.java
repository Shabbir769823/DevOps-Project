package com.devops.employee.repository;

import com.devops.employee.model.Notification;
import com.devops.employee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndReadStatusOrderByCreatedAtDesc(User recipient, boolean readStatus);
    long countByRecipientAndReadStatus(User recipient, boolean readStatus);
}
