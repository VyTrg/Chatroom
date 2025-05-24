package com.example.chatroom.controller;

import com.example.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.chatroom.model.Message;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/latest/{userId}")
    public List<Message> getLatestMessagesForUser(@PathVariable Long userId) {
        return messageService.getLatestMessagesForUser(userId);
    }

    @GetMapping("/conversation/{conversationId}")
    public List<Message> getMessagesByConversation(@PathVariable Long conversationId) {
        return messageService.getMessagesByConversation(conversationId);
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/conversation/{conversationId}/clear")
    public ResponseEntity<Void> clearConversation(@PathVariable Long conversationId) {
        messageService.deleteAllMessagesInConversation(conversationId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long messageId, @RequestBody Map<String, String> payload) {
        String newContent = payload.get("content");
        Message updatedMessage = messageService.updateMessage(messageId, newContent);
        return ResponseEntity.ok(updatedMessage);
    }
}
