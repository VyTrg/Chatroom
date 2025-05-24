package com.example.chatroom.controller;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.service.ContactWithServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
public class ContactWithController {
    @Autowired
    private ContactWithServiceImpl contactWithService;

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
            Map<String, Object> contactInfo = Map.of(
                    "id", contact.getId(),
                    "user1", contact.getContactOne().getId(),
                    "user2", contact.getContactTwo().getId()
            );

            contactWithService.deleteContactWith(contact);

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
    
    // Tìm liên hệ theo ID của hai người dùng (có thể theo cả hai chiều)
    @GetMapping("/find-by-users")
    public ResponseEntity<ContactWith> findContactByUsers(
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
