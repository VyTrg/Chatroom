package com.example.chatroom.controller;

import com.example.chatroom.chat.MessageType;
import com.example.chatroom.chat.NotificationMessage;
import com.example.chatroom.dto.ConversationWithRoleDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.service.ContactWithService;
import com.example.chatroom.service.ConservationServiceImpl;
import com.example.chatroom.service.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ContactWithService contactWithService;

    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ConservationServiceImpl conservationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/conversations/user/{userId}")
    public List<ConversationWithRoleDTO> getConversationsForUser(@PathVariable Long userId) {
        return conservationService.getAllConversationsForUserWithRole(userId);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversations/{conversationId}/rename")
    public ResponseEntity<Conversation> renameConversation(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> nameRequest,
            @RequestHeader("Authorization") String token) {
        String newName = nameRequest.get("name");
        Conversation renamed = conversationService.renameConversation(conversationId, newName);
        
        // Lấy thông tin người thực hiện từ token và đổi tên
        String tokenValue = token.replace("Bearer ", "");
        Long initiatorId = null;
        try {
            initiatorId = Long.valueOf(token.split("\\.")[0]); // Đơn giản hóa, trong thực tế nên dùng JWT parser
        } catch (Exception e) {
            // Nếu không lấy được ID từ token, vẫn tiếp tục nhưng không gửi initiatorId
        }
        
        // Gửi thông báo WebSocket đến tất cả thành viên cuộc trò chuyện
        List<ConversationMember> members = conversationService.getConversationMembers(conversationId);
        for (ConversationMember member : members) {
            // Bỏ qua người thực hiện đổi tên (nếu có)
            if (initiatorId != null && member.getUser().getId().equals(initiatorId)) {
                continue;
            }
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "RENAME");
            payload.put("conversationId", conversationId);
            payload.put("newName", newName);
            if (initiatorId != null) {
                payload.put("initiatorId", initiatorId);
            }
            
            messagingTemplate.convertAndSendToUser(
                member.getUser().getId().toString(),
                "/queue/conversation-changes",
                payload
            );
        }
        
        return ResponseEntity.ok(renamed);
    }

    @GetMapping("/conversations/{conversationId}/members")
    public List<ConversationMember> getConversationMembers(@PathVariable Long conversationId) {
        return conversationService.getConversationMembers(conversationId);
    }

    @GetMapping("/conversations/by-contact/{contactId}")
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

    @GetMapping("/group-conversations/user/{userId}")
    public List<Conversation> getAllConversationsForUser(@PathVariable Long userId) {
        return conservationService.getAllConversationsForUser(userId);
    }
    
    @PostMapping("/group-conversations/group")
    public ResponseEntity<Conversation> createGroupConversation(
            @RequestBody List<Long> userIds,
            @RequestParam(required = false) String conversationName) {
        try {
            Conversation conversation = conservationService.createGroupConversation(userIds, conversationName);
            for (Long userId : userIds) {
                NotificationMessage notification = new NotificationMessage();
                notification.setType(MessageType.valueOf("GROUP_REQUEST"));  // message type for group notifications
                notification.setMessage("You have been added to a new group: " + (conversationName != null ? conversationName : "Unnamed Group"));
                notification.setUserId(userId);  // the user who receives this notification

                messagingTemplate.convertAndSend(
                        "/topic/notifications/" + userId,  // topic specific to this user
                        notification
                );
            }
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
