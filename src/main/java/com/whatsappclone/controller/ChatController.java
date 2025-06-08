package com.whatsappclone.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.whatsappclone.model.Message;
import com.whatsappclone.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam String user1, @RequestParam String user2) {
        return chatService.getChatHistory(user1, user2);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        chatService.saveMessage(message);
        return ResponseEntity.ok().build();
    }
}