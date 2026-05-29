package com.devops.employee.service;

import com.devops.employee.model.ChatMessage;
import com.devops.employee.model.User;
import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(User sender, User recipient, String content);
    List<ChatMessage> getChatHistory(User u1, User u2);
    long getUnreadCount(User recipient);
    void markAsRead(User recipient, User sender);
}
