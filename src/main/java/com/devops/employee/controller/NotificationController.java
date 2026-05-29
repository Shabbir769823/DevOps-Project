package com.devops.employee.controller;

import com.devops.employee.model.Notification;
import com.devops.employee.model.User;
import com.devops.employee.service.NotificationService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public String listNotifications(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) {
            return "redirect:/dashboard?error=AccessDenied";
        }

        List<Notification> notifications = notificationService.getNotificationsForUser(currentUser);
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", currentUser);
        return "notifications/list";
    }

    @PostMapping("/read/{id}")
    public String markAsRead(@PathVariable("id") Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) {
            return "redirect:/dashboard?error=AccessDenied";
        }

        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = userService.findByUsername(auth.getName());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName()));
        if (!isAdmin) {
            return "redirect:/dashboard?error=AccessDenied";
        }

        List<Notification> unread = notificationService.getUnreadNotificationsForUser(currentUser);
        for (Notification notification : unread) {
            notificationService.markAsRead(notification.getId());
        }
        return "redirect:/notifications";
    }
}
