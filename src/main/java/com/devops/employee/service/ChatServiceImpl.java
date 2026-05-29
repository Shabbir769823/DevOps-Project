package com.devops.employee.service;

import com.devops.employee.model.ChatMessage;
import com.devops.employee.model.User;
import com.devops.employee.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public ChatMessage saveMessage(User sender, User recipient, String content) {
        ChatMessage message = new ChatMessage(sender, recipient, content.trim(), LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(User u1, User u2) {
        return chatMessageRepository.findConversationHistory(u1, u2);
    }

    @Override
    public long getUnreadCount(User recipient) {
        return chatMessageRepository.countByRecipientAndIsReadFalse(recipient);
    }

    @Override
    public void markAsRead(User recipient, User sender) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByRecipientAndSenderAndIsReadFalse(recipient, sender);
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
        }
        chatMessageRepository.saveAll(unreadMessages);
    }
}
