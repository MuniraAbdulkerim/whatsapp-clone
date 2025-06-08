package com.whatsapp.service;

import com.whatsapp.model.Message;
import java.util.List;

public interface MessageService {
    Message saveMessage(Message message);
    List<Message> getMessagesBetweenUsers(Long user1Id, Long user2Id);
    void markMessageAsRead(Long messageId);
    List<Message> getUnreadMessages(Long userId);
} 