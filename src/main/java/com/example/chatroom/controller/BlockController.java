package com.example.chatroom.controller;

import com.example.chatroom.service.BlockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class BlockController {

    @Autowired
    private BlockServiceImpl blockService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockUser(@RequestParam("blocker") Long blocker, @RequestParam("blocked") Long blocked) {
        // Gọi service để block user
        blockService.blockUser(blocker, blocked);
        
        // Gửi thông báo WebSocket đến người bị chặn
        Map<String, Object> blockedPayload = new HashMap<>();
        blockedPayload.put("type", "BLOCKED");
        blockedPayload.put("userId", blocked);
        blockedPayload.put("initiatorId", blocker);
        blockedPayload.put("targetId", blocked);
        blockedPayload.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSendToUser(
                blocked.toString(),
                "/queue/contact-changes",
                blockedPayload
        );
        
        // Thông báo xác nhận cho người chặn
        Map<String, Object> blockerPayload = new HashMap<>();
        blockerPayload.put("type", "BLOCK_CONFIRMATION");
        blockerPayload.put("targetId", blocked);
        blockerPayload.put("message", "Bạn đã chặn liên hệ thành công");
        
        messagingTemplate.convertAndSendToUser(
                blocker.toString(),
                "/queue/contact-changes",
                blockerPayload
        );
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã chặn người dùng thành công",
                "blockerId", blocker,
                "blockedId", blocked
        ));
    }

    @PostMapping("/unblock")
    public ResponseEntity<Void> unblockUser(@RequestParam("blocker") Long blocker, @RequestParam("blocked") Long blocked) {
        // Gọi service để unblock
        blockService.unblockUser(blocker, blocked);
        
        // Gửi thông báo WebSocket đến cả hai người dùng
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "UNBLOCKED");
        payload.put("userId", blocked);  
        payload.put("initiatorId", blocker);
        payload.put("targetId", blocked);  
        payload.put("timestamp", LocalDateTime.now().toString());

        // Thông báo cho người được bỏ chặn
        messagingTemplate.convertAndSendToUser(
                blocked.toString(),
                "/queue/contact-changes",
                payload
        );

        // Thông báo xác nhận cho người bỏ chặn
        payload.put("type", "UNBLOCK_CONFIRMATION");
        payload.put("targetId", blocked);  
        payload.put("message", "Bạn đã bỏ chặn liên hệ thành công");
        messagingTemplate.convertAndSendToUser(
                blocker.toString(),
                "/queue/contact-changes",
                payload
        );
        
        return ResponseEntity.ok().build();
    }
}
