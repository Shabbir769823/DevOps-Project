package com.devops.employee.controller;

import com.devops.employee.model.ChatMessage;
import com.devops.employee.model.User;
import com.devops.employee.service.ChatService;
import com.devops.employee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return null;
        }
        return userService.findByUsername(auth.getName());
    }

    @GetMapping
    public String showChatPage(@RequestParam(value = "userId", required = false) Long targetUserId, Model model) {
        User currentUser = getLoggedInUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);

        // Check if user is admin
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            // Admin lists all other users (employees)
            List<User> employees = userService.getAllUsers().stream()
                    .filter(u -> !u.getUsername().equals(currentUser.getUsername()))
                    .collect(Collectors.toList());
            model.addAttribute("chatUsers", employees);

            if (targetUserId != null) {
                User targetUser = userService.findById(targetUserId);
                chatService.markAsRead(currentUser, targetUser);
                List<ChatMessage> history = chatService.getChatHistory(currentUser, targetUser);
                model.addAttribute("targetUser", targetUser);
                model.addAttribute("history", history);
            }
        } else {
            // Employee chats with the primary admin
            User adminUser = userService.getFirstAdmin();
            chatService.markAsRead(currentUser, adminUser);
            List<ChatMessage> history = chatService.getChatHistory(currentUser, adminUser);
            model.addAttribute("targetUser", adminUser);
            model.addAttribute("history", history);
        }

        return "chat";
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<?> sendMessage(@RequestParam("recipientId") Long recipientId,
                                         @RequestParam("content") String content) {
        User currentUser = getLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message content cannot be empty");
        }

        User recipient = userService.findById(recipientId);
        ChatMessage msg = chatService.saveMessage(currentUser, recipient, content);

        return ResponseEntity.ok(Map.of("status", "success", "messageId", msg.getId()));
    }

    @GetMapping("/history/{targetUserId}")
    public String getChatHistoryFragment(@PathVariable("targetUserId") Long targetUserId, Model model) {
        User currentUser = getLoggedInUser();
        if (currentUser == null) {
            return "login";
        }

        User targetUser = userService.findById(targetUserId);
        chatService.markAsRead(currentUser, targetUser);
        List<ChatMessage> history = chatService.getChatHistory(currentUser, targetUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("history", history);

        return "chat :: messageHistory";
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount() {
        User currentUser = getLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.ok(0L);
        }
        return ResponseEntity.ok(chatService.getUnreadCount(currentUser));
    }
}
