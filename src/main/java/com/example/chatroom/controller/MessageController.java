package com.example.chatroom.controller;

import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.chatroom.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

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
        // Xóa tin nhắn trong cuộc trò chuyện
        messageService.deleteAllMessagesInConversation(conversationId);
        
        // Tìm tất cả người dùng trong cuộc trò chuyện để gửi thông báo real-time
        List<ConversationMember> participants = conversationMemberRepository.findAllByConversationId(conversationId);
        
        // Gửi thông báo WebSocket đến tất cả người dùng trong cuộc trò chuyện
        for (ConversationMember member : participants) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "CLEAR_HISTORY");
            payload.put("conversationId", conversationId);
            
            messagingTemplate.convertAndSendToUser(
                member.getUser().getId().toString(),
                "/queue/conversation-cleared",
                payload
            );
        }
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long messageId, @RequestBody Map<String, String> payload) {
        String newContent = payload.get("content");
        Message updatedMessage = messageService.updateMessage(messageId, newContent);
        return ResponseEntity.ok(updatedMessage);
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }
}
