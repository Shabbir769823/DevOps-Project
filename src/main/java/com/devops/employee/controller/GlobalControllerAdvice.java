package com.devops.employee.controller;

import com.devops.employee.model.User;
import com.devops.employee.service.NotificationService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public GlobalControllerAdvice(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                User user = userService.findByUsername(auth.getName());
                if (user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()))) {
                    return notificationService.getUnreadCountForUser(user);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return 0;
    }
}
