package com.example.chatroom.controller;

import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.security.JwtUtil;
import com.example.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.chatroom.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

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
        Message message = messageService.markMessageAsRead(messageId);
        
        // Gửi thông báo WebSocket về trạng thái đã đọc
        if (message != null && message.getSender() != null && message.getConversation() != null) {
            Map<String, Object> readStatusUpdate = new HashMap<>();
            readStatusUpdate.put("type", "READ_STATUS");
            readStatusUpdate.put("messageId", messageId);
            readStatusUpdate.put("conversationId", message.getConversation().getId());
            readStatusUpdate.put("timestamp", java.time.LocalDateTime.now().toString());
            
            // Gửi thông báo đến người gửi tin nhắn
            messagingTemplate.convertAndSendToUser(
                message.getSender().getId().toString(),
                "/queue/read-status",
                readStatusUpdate
            );
        }
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(@PathVariable Long conversationId, @RequestHeader("Authorization") String authHeader) {
        // Lấy userId từ token để biết ai đang đánh dấu đọc
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long currentUserId = null;
        
        try {
            // Lấy username từ token
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                // Lấy userId từ username
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    currentUserId = userOpt.get().getId();
                    
                    // Đánh dấu tất cả tin nhắn trong cuộc trò chuyện là đã đọc
                    List<Message> messages = messageService.markAllMessagesAsReadInConversation(conversationId, currentUserId);
                    
                    // Gửi thông báo WebSocket về trạng thái đã đọc cho cả cuộc trò chuyện
                    Map<String, Object> readStatusUpdate = new HashMap<>();
                    readStatusUpdate.put("type", "READ_STATUS");
                    readStatusUpdate.put("conversationId", conversationId);
                    readStatusUpdate.put("readByUserId", currentUserId);
                    readStatusUpdate.put("timestamp", java.time.LocalDateTime.now().toString());
                    
                    // Gửi thông báo đến tất cả thành viên trong cuộc trò chuyện
                    List<ConversationMember> members = conversationMemberRepository.findAllByConversationId(conversationId);
                    for (ConversationMember member : members) {
                        messagingTemplate.convertAndSendToUser(
                            member.getUser().getId().toString(),
                            "/queue/read-status",
                            readStatusUpdate
                        );
                    }
                    
                    return ResponseEntity.ok().build();
                }
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
