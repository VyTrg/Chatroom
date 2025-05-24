package com.example.chatroom.controller;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.service.ContactWithService;
import com.example.chatroom.service.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private ContactWithService contactWithService;
    
    @Autowired
    private ConversationRepository conversationRepository;

    @GetMapping("/user/{userId}")
    public List<Conversation> getConversationsForUser(@PathVariable Long userId) {
        return conversationService.getConversationsForUser(userId);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{conversationId}/rename")
    public ResponseEntity<Conversation> renameConversation(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> nameRequest) {
        String newName = nameRequest.get("name");
        Conversation renamed = conversationService.renameConversation(conversationId, newName);
        return ResponseEntity.ok(renamed);
    }

    @GetMapping("/{conversationId}/members")
    public List<ConversationMember> getConversationMembers(@PathVariable Long conversationId) {
        return conversationService.getConversationMembers(conversationId);
    }

    @GetMapping("/by-contact/{contactId}")
    public ResponseEntity<?> getConversationsByContactId(@PathVariable Long contactId) {
        try {
            // Lấy thông tin liên hệ
            ContactWith contact = contactWithService.getContactWithById(contactId);
            if (contact == null) {
                return ResponseEntity.notFound().build();
            }

            // Tìm cuộc trò chuyện 1-1 giữa hai người dùng
            Long user1Id = contact.getContactOne().getId();
            Long user2Id = contact.getContactTwo().getId();

            // Lấy danh sách cuộc trò chuyện của cả hai người dùng
            List<Conversation> user1Conversations = conversationRepository.findAllConversationsForUser(user1Id);
            List<Conversation> user2Conversations = conversationRepository.findAllConversationsForUser(user2Id);

            // Tìm cuộc trò chuyện chung (cuộc trò chuyện có cả hai người dùng)
            List<Conversation> sharedConversations = user1Conversations.stream()
                    .filter(user2Conversations::contains)
                    .filter(conv -> !conv.getIsGroup()) // Chỉ lấy cuộc trò chuyện 1-1, không phải nhóm
                    .collect(Collectors.toList());

            // Đảm bảo trả về mảng rỗng thay vì null
            if (sharedConversations == null) {
                sharedConversations = new ArrayList<>();
            }

            // Ghi log để debug
            System.out.println("Tìm thấy " + sharedConversations.size() + " cuộc trò chuyện giữa users: " + user1Id + " và " + user2Id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("conversations", sharedConversations);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi tìm cuộc trò chuyện: " + e.getMessage()));
        }
    }
}
