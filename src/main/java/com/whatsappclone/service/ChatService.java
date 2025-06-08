package com.whatsappclone.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.whatsappclone.model.Message;

@Service
public class ChatService {
    // Example method signatures
    public List<Message> getChatHistory(String user1, String user2) {
        // TODO: Implement this method to fetch from DB
        return null;
    }
    public void saveMessage(Message msg) {
        // TODO: Implement this method to save to DB
    }
}