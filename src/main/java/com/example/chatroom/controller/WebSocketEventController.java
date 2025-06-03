package com.example.chatroom.controller;

import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý các sự kiện WebSocket không liên quan đến chat
 */
@Controller
public class WebSocketEventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Xử lý khi người dùng thay đổi trạng thái (online, offline, away)
     */
    @MessageMapping("/user.status")
    public void handleUserStatusChange(Map<String, Object> statusUpdate) {
        try {
            Long userId = Long.parseLong(statusUpdate.get("userId").toString());
            String status = statusUpdate.get("status").toString();
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Tạo thông báo cập nhật trạng thái
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "USER_STATUS");
                notification.put("userId", userId);
                notification.put("username", user.getUsername());
                notification.put("status", status);
                notification.put("timestamp", System.currentTimeMillis());
                
                // Gửi cho tất cả người dùng (có thể lọc theo danh sách bạn bè trong tương lai)
                messagingTemplate.convertAndSend("/topic/user.status", notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý khi người dùng đánh dấu tin nhắn đã đọc
     */
    @MessageMapping("/message.read")
    public void handleMessageRead(Map<String, Object> readReceipt) {
        try {
            // Debug logging
            System.out.println("[DEBUG] Received read receipt: " + readReceipt);
            
            if (readReceipt == null) {
                System.err.println("[ERROR] Read receipt is null");
                return;
            }
            
            // Kiểm tra null và lấy dữ liệu an toàn
            Object messageIdObj = readReceipt.get("messageId");
            Object senderIdObj = readReceipt.get("senderId");
            
            if (messageIdObj == null) {
                System.err.println("[ERROR] messageId is missing in read receipt");
                return;
            }
            
            if (senderIdObj == null) {
                System.err.println("[ERROR] senderId is missing in read receipt");
                return;
            }
            
            Long messageId;
            Long senderId;
            
            try {
                messageId = Long.parseLong(messageIdObj.toString());
            } catch (NumberFormatException e) {
                System.err.println("[ERROR] Invalid messageId format: " + messageIdObj);
                return;
            }
            
            try {
                senderId = Long.parseLong(senderIdObj.toString());
            } catch (NumberFormatException e) {
                System.err.println("[ERROR] Invalid senderId format: " + senderIdObj);
                return;
            }
            
            // Tạo thông báo đã đọc tin nhắn
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "READ_STATUS");
            notification.put("messageId", messageId);
            
            // Xử lý conversationId an toàn
            Object conversationIdObj = readReceipt.get("conversationId");
            if (conversationIdObj != null) {
                notification.put("conversationId", conversationIdObj);
            } else {
                // Nếu không có conversationId, tìm từ message trong DB
                Optional<Message> messageOpt = messageRepository.findById(messageId);
                if (messageOpt.isPresent() && messageOpt.get().getConversation() != null) {
                    notification.put("conversationId", messageOpt.get().getConversation().getId());
                    System.out.println("[DEBUG] Found conversationId from database: " + 
                                       messageOpt.get().getConversation().getId());
                } else {
                    System.err.println("[WARNING] Could not find conversationId for messageId: " + messageId);
                }
            }
            
            notification.put("timestamp", System.currentTimeMillis());
            
            // Gửi thông báo cho người gửi tin nhắn
            messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/read-status",
                notification
            );
            
            System.out.println("[DEBUG] Sent read status notification to user: " + senderId);
        } catch (Exception e) {
            System.err.println("[ERROR] Error in handleMessageRead: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gửi thông báo khi có tin nhắn mới đến cho người dùng
     */
    public void sendNewMessageNotification(Message message, Long recipientId) {
        try {
            // Tạo thông báo chứa thông tin cần thiết
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NEW_MESSAGE");
            notification.put("messageId", message.getId());
            notification.put("conversationId", message.getConversation().getId());
            notification.put("senderId", message.getSender().getId());
            notification.put("senderName", message.getSender().getUsername());
            notification.put("content", message.getMessageText());
            notification.put("timestamp", System.currentTimeMillis());
            
            // Gửi thông báo đến người nhận
            messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/new-message-notification",
                notification
            );
            
            System.out.println("[DEBUG] Đã gửi thông báo tin nhắn mới đến người dùng: " + recipientId);
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi gửi thông báo tin nhắn mới: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
