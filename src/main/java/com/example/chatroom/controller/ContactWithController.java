package com.example.chatroom.controller;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.service.ContactWithServiceImpl;
import com.example.chatroom.service.ConversationService;
import com.example.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactWithController {
    @Autowired
    private ContactWithServiceImpl contactWithService;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Lấy tất cả liên hệ
    @GetMapping
    public ResponseEntity<List<ContactWith>> getContacts() {
        return ResponseEntity.ok(contactWithService.getAllContactsWith());
    }

    // Lấy liên hệ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ContactWith> getContactByUserId(@PathVariable("id") Long id) {
        ContactWith contactWith = contactWithService.getContactWithByUserId(id);
        if (contactWith == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contactWith);
    }

    // Tạo liên hệ mới (gửi lời mời kết bạn)
    @PostMapping
    public ResponseEntity<ContactWith> createContact(@RequestBody ContactWith contactWith) {
        ContactWith saved = contactWithService.saveContactWith(contactWith);
        return ResponseEntity.ok(saved);
    }

    // Cập nhật liên hệ
    @PutMapping("/{id}")
    public ResponseEntity<ContactWith> updateContact(@PathVariable("id") Long id, @RequestBody ContactWith contactWith) {
        ContactWith existing = contactWithService.getContactWithById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        // Cập nhật các trường cần thiết
        existing.setContactOne(contactWith.getContactOne());
        existing.setContactTwo(contactWith.getContactTwo());
        existing.setCreatedAt(contactWith.getCreatedAt());
        existing.setDeletedAt(contactWith.getDeletedAt());
        ContactWith updated = contactWithService.updateContactWith(existing);
        return ResponseEntity.ok(updated);
    }

    // Xóa liên hệ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable("id") Long id) {
        try {
            ContactWith contact = contactWithService.getContactWithById(id);
            if (contact == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Không tìm thấy liên hệ với ID: " + id));
            }

            // Lưu thông tin liên hệ trước khi xóa để frontend có thể sử dụng
            Long user1Id = contact.getContactOne().getId();
            Long user2Id = contact.getContactTwo().getId();
            
            Map<String, Object> contactInfo = Map.of(
                    "id", contact.getId(),
                    "user1", user1Id,
                    "user2", user2Id
            );

            contactWithService.deleteContactWith(contact);
            
            // Gửi thông báo WebSocket đến cả hai người dùng
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "DELETE");
            payload.put("contactId", id);
            payload.put("initiatorId", user1Id); // Giả định người xóa là user1
            payload.put("timestamp", LocalDateTime.now().toString());
            
            // Thông báo cho người dùng thứ nhất
            payload.put("userId", user2Id);
            messagingTemplate.convertAndSendToUser(
                user1Id.toString(),
                "/queue/contact-changes",
                payload
            );
            
            // Thông báo cho người dùng thứ hai
            payload.put("userId", user1Id);
            messagingTemplate.convertAndSendToUser(
                user2Id.toString(),
                "/queue/contact-changes",
                payload
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xóa liên hệ thành công",
                    "contactInfo", contactInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Lỗi khi xóa liên hệ: " + e.getMessage()));
        }
    }

    /**
     * Tìm contact dựa trên ID của hai người dùng
     */
//    @GetMapping("/find-by-users")
//    public ResponseEntity<?> findContactByUsers(
//            @RequestParam Long userId,
//            @RequestParam Long contactId) {
//        try {
//            ContactWith contact = contactWithService.findContactBetweenUsers(userId, contactId);
//            if (contact == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Collections.singletonMap("message", "Không tìm thấy liên hệ giữa hai người dùng"));
//            }
//            return ResponseEntity.ok(contact);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("message", "Lỗi khi tìm liên hệ: " + e.getMessage()));
//        }
//    }
    @GetMapping("/find-by-users")
    public ResponseEntity<?> findContactByUsers(
            @RequestParam Long userId,
            @RequestParam Long contactId) {
        try {
            // Log tham số đầu vào
            System.out.println("Tìm liên hệ với userId: " + userId + ", contactId: " + contactId);

            // Kiểm tra tham số
            if (userId == null || contactId == null) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("message", "Thiếu tham số userId hoặc contactId"));
            }

            ContactWith contact = contactWithService.findContactBetweenUsers(userId, contactId);
            System.out.println("Kết quả tìm liên hệ: " + (contact != null ? "Tìm thấy ID=" + contact.getId() : "Không tìm thấy"));

            if (contact == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Không tìm thấy liên hệ giữa hai người dùng"));
            }

//            // Thử trả về chỉ ID của contact thay vì toàn bộ đối tượng
//            return ResponseEntity.ok(Collections.singletonMap("id", contact.getId()));
            // Trả về Map chứa các thông tin cần thiết thay vì toàn bộ đối tượng để tránh lỗi serialization
            Map<String, Object> result = new HashMap<>();
            result.put("id", contact.getId());
            // Loại bỏ trường conversationId vì không có phương thức tương ứng trong entity
            result.put("contactOneId", contact.getContactOne().getId());
            result.put("contactTwoId", contact.getContactTwo().getId());
            result.put("createdAt", contact.getCreatedAt());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi vào log server
            System.out.println("Lỗi chi tiết: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Lỗi khi tìm liên hệ: " + e.getMessage()));
        }
    }
    
    /**
     * Xóa contact và conversation liên quan dựa trên conversationId
     */
    @DeleteMapping("/delete-contact")
    public ResponseEntity<?> deleteContact(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long contactId,
            @RequestParam(required = false) Long conversationId) {
        try {
            if (conversationId != null) {
                // Nếu có conversationId, xóa trực tiếp bằng conversationId
                List<ConversationMember> members = conversationMemberRepository.findAllByConversationId(conversationId);

                if (members.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("message", "Không tìm thấy cuộc trò chuyện"));
                }

                // Lấy conversation từ member đầu tiên
                Conversation conversation = members.get(0).getConversation();

                if (conversation.getIsGroup()) {
                    if (userId != null) {
                        boolean isAdmin = conversationMemberRepository.isUserAdmin(userId, conversationId);
                        if (!isAdmin) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Collections.singletonMap("message", "Bạn không có quyền xóa nhóm"));
                        }
                    }
                    try {
                        // Xóa tất cả tin nhắn trong cuộc trò chuyện (đã bao gồm xóa attachments)
                        messageService.deleteAllMessagesByConversationId(conversationId);
                        conversation.setDeletedAt(LocalDateTime.now());
                        conversationService.save(conversation);

                        // Gửi thông báo WebSocket đến tất cả thành viên trong nhóm
                        for (ConversationMember member : members) {
                            Long memberId = member.getUser().getId();
                            
                            Map<String, Object> payload = new HashMap<>();
                            payload.put("type", "GROUP_DELETED");
                            payload.put("conversationId", conversationId);
                            payload.put("initiatorId", userId);
                            payload.put("timestamp", LocalDateTime.now().toString());

                            messagingTemplate.convertAndSendToUser(
                                memberId.toString(),
                                "/queue/contact-changes",
                                payload
                            );
                        }

                        return ResponseEntity.ok(Map.of(
                                "message", "Đã xóa nhóm thành công",
                                "conversationId", conversationId,
                                "isGroup", true
                        ));
                    } catch (Exception e) {
                        System.err.println("Lỗi: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.singletonMap("message", "Lỗi xóa nhóm" + e.getMessage()));

                    }

                }else {
                    // Xử lý cuộc trò chuyện cá nhân
                    if (members.size() == 2) {
                        // Lấy ID người dùng từ các thành viên
                        Long user1 = members.get(0).getUser().getId();
                        Long user2 = members.get(1).getUser().getId();
                        
                        // Xác định initiatorId (người thực hiện xóa)
                        Long initiatorId = userId != null ? userId : user1;
                        Long targetId = initiatorId.equals(user1) ? user2 : user1;

                        // Tìm contact giữa hai người dùng - sử dụng contactWithService thay vì repository
                        ContactWith contact = contactWithService.findContactBetweenUsers(user1, user2);
                        if (contact != null) {
                            // Đánh dấu contact đã bị xóa (soft delete)
                            contact.setDeletedAt(LocalDateTime.now());
                            contactWithService.save(contact);
                            
                            // Gửi thông báo WebSocket cho cả hai người dùng
                            Map<String, Object> payload = new HashMap<>();
                            payload.put("type", "DELETE");
                            payload.put("contactId", contact.getId());
                            payload.put("initiatorId", initiatorId);
                            payload.put("timestamp", LocalDateTime.now().toString());
                            
                            // Thông báo cho người bị xóa liên hệ
                            payload.put("userId", initiatorId);
                            messagingTemplate.convertAndSendToUser(
                                targetId.toString(),
                                "/queue/contact-changes",
                                payload
                            );
                            
                            // Thông báo xác nhận cho người xóa liên hệ
                            payload.put("type", "DELETE_CONFIRMATION");
                            payload.put("userId", targetId);
                            payload.put("message", "Bạn đã xóa liên hệ thành công");
                            messagingTemplate.convertAndSendToUser(
                                initiatorId.toString(),
                                "/queue/contact-changes",
                                payload
                            );
                        }
                    }

                    try {
                        // Xóa tất cả tin nhắn trong cuộc trò chuyện (đã bao gồm xóa attachments)
                        messageService.deleteAllMessagesByConversationId(conversationId);

                        // Đánh dấu conversation đã bị xóa (soft delete)
                        conversation.setDeletedAt(LocalDateTime.now());
                        conversationService.save(conversation);

                        return ResponseEntity.ok(Map.of(
                                "message", "Đã xóa liên hệ và cuộc trò chuyện thành công",
                                "conversationId", conversationId,
                                "isGroup", false
                        ));
                    } catch (Exception e) {
                        System.err.println("Lỗi chi tiết: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.singletonMap("message", "Lỗi khi xóa cuộc trò chuyện: " + e.getMessage()));
                    }
                }
            } else if (userId != null && contactId != null) {
                // Tìm contact giữa hai người dùng
                ContactWith contact = contactWithService.findContactBetweenUsers(userId, contactId);

                if (contact == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("message", "Không tìm thấy liên hệ giữa hai người dùng"));
                }

                // Tìm conversation liên quan đến contact này
                List<Conversation> conversations = conversationService.findByContactId(contact.getId());

                // Xóa tất cả tin nhắn trong các conversation và đánh dấu conversation đã bị xóa
                for (Conversation conversation : conversations) {
                    // Xóa tin nhắn và attachments
                    messageService.deleteAllMessagesByConversationId(conversation.getId());

                    // Đánh dấu conversation đã bị xóa (soft delete)
                    conversation.setDeletedAt(LocalDateTime.now());
                    conversationService.save(conversation);
                }

                // Đánh dấu contact đã bị xóa (soft delete)
                contact.setDeletedAt(LocalDateTime.now());
                contactWithService.save(contact);
                
                // Gửi thông báo WebSocket cho người bị xóa liên hệ
                Map<String, Object> payload = new HashMap<>();
                payload.put("type", "DELETE");
                payload.put("contactId", contact.getId());
                payload.put("initiatorId", userId);
                payload.put("timestamp", LocalDateTime.now().toString());
                payload.put("userId", userId);
                
                messagingTemplate.convertAndSendToUser(
                    contactId.toString(),
                    "/queue/contact-changes",
                    payload
                );
                
                // Thông báo xác nhận cho người xóa liên hệ
                Map<String, Object> confirmationPayload = new HashMap<>();
                confirmationPayload.put("type", "DELETE_CONFIRMATION");
                confirmationPayload.put("contactId", contact.getId());
                confirmationPayload.put("userId", contactId);
                confirmationPayload.put("message", "Bạn đã xóa liên hệ thành công");
                
                messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/contact-changes",
                    confirmationPayload
                );
                
                return ResponseEntity.ok(Map.of(
                        "message", "Đã xóa liên hệ và cuộc trò chuyện thành công",
                        "contactId", contact.getId()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "Thiếu tham số: cần cung cấp conversationId hoặc cả userId và contactId"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Lỗi khi xóa liên hệ: " + e.getMessage()));
        }
    }
    
    // Tìm liên hệ theo ID của hai người dùng (có thể theo cả hai chiều)
    @GetMapping("/find-by-users-old")
    public ResponseEntity<ContactWith> findContactByUsersOld(
            @RequestParam("userId") Long userId,
            @RequestParam("contactId") Long contactId) {

        // Tìm theo cả hai chiều (user1->user2 hoặc user2->user1)
        ContactWith contact = contactWithService.getContactWithByUserIds(userId, contactId);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contact);
    }
}
