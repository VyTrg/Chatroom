package com.example.chatroom.controller;

import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller để xử lý các sự kiện WebSocket
 */
@Controller
public class WebSocketEventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;


    @MessageMapping("/user.status")
    public void handleUserStatusChange(Map<String, Object> statusUpdate) {
        try {
            Long userId = Long.parseLong(statusUpdate.get("userId").toString());
            String status = statusUpdate.get("status").toString();
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Cập nhật map onlineUsers
                if (status.equalsIgnoreCase("online") || status.equalsIgnoreCase("away")) {
                    onlineUsers.put(userId, status.toLowerCase());
                    System.out.println("[DEBUG] Cập nhật trạng thái: userId=" + userId + ", status=" + status);
                } else if (status.equalsIgnoreCase("offline")) {
                    onlineUsers.remove(userId);
                    System.out.println("[DEBUG] Xóa khỏi danh sách online: userId=" + userId);
                }
                
                // Tạo thông báo cập nhật trạng thái
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "USER_STATUS");
                notification.put("userId", userId);
                notification.put("username", user.getUsername());
                notification.put("status", status.toLowerCase());  // Đảm bảo status lowercase
                notification.put("timestamp", System.currentTimeMillis());
                
                // Gửi cho tất cả người dùng (có thể lọc theo danh sách bạn bè trong tương lai)
                messagingTemplate.convertAndSend("/topic/user.status", notification);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi cập nhật trạng thái người dùng: " + e.getMessage());
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

    private static final Map<Long, String> onlineUsers = new ConcurrentHashMap<>();

    @MessageMapping("/user.all-status")
    public void handleGetAllUserStatus(Map<String, Object> request) {
        try {
            // Lấy username của người yêu cầu từ request
            String requestingUsername = null;
            if (request.containsKey("username")) {
                requestingUsername = (String) request.get("username");
            } else if (request.containsKey("userId")) {
                // Nếu không có username nhưng có userId, tìm username từ userId
                Long requestingUserId = Long.parseLong(request.get("userId").toString());
                Optional<User> userOpt = userRepository.findById(requestingUserId);
                if (userOpt.isPresent()) {
                    requestingUsername = userOpt.get().getUsername();
                    System.out.println("[DEBUG] Tìm username từ userId: " + requestingUserId + " -> " + requestingUsername);
                } else {
                    System.err.println("[ERROR] Không tìm thấy người dùng với userId: " + requestingUserId);
                    return;
                }
            } else {
                System.err.println("[ERROR] Yêu cầu không chứa username hoặc userId");
                return;
            }

            System.out.println("[DEBUG] Nhận yêu cầu danh sách online từ: " + requestingUsername);
            System.out.println("[DEBUG] Danh sách người dùng online hiện tại: " + onlineUsers.size() + " người dùng");

            // Gửi lại trạng thái của từng người dùng đang online cho người dùng vừa kết nối
            for (Map.Entry<Long, String> entry : onlineUsers.entrySet()) {
                Long userId = entry.getKey();
                String status = entry.getValue();

                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "USER_STATUS");
                    notification.put("userId", userId);
                    notification.put("username", user.getUsername());
                    notification.put("status", status);
                    notification.put("timestamp", System.currentTimeMillis());

                    // Gửi riêng cho người yêu cầu
                    messagingTemplate.convertAndSendToUser(
                            requestingUsername,
                            "/queue/user.status",
                            notification
                    );
                    
                    System.out.println("[DEBUG] Đã gửi thông báo trạng thái của userId=" + userId + 
                                     " (status=" + status + ") đến " + requestingUsername);
                }
            }
            
            if (onlineUsers.isEmpty()) {
                System.out.println("[DEBUG] Không có người dùng online để gửi đến " + requestingUsername);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi xử lý yêu cầu lấy danh sách người dùng online: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * REST API endpoint để lấy danh sách người dùng đang online
     * Sử dụng khi trang được tải, trước khi WebSocket kết nối
     */
    @GetMapping("/api/users/online")
    @ResponseBody
    public List<Map<String, Object>> getOnlineUsers() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map.Entry<Long, String> entry : onlineUsers.entrySet()) {
            Long userId = entry.getKey();
            String status = entry.getValue();
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Map<String, Object> userStatus = new HashMap<>();
                userStatus.put("userId", userId);
                userStatus.put("username", user.getUsername());
                userStatus.put("status", status);
                
                result.add(userStatus);
            }
        }
        
        return result;
    }

    @PostMapping("/api/users/status")
    @ResponseBody
    public Map<String, Object> updateUserStatus(@RequestBody Map<String, Object> statusUpdate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = Long.parseLong(statusUpdate.get("userId").toString());
            String username = statusUpdate.get("username").toString();
            String status = statusUpdate.get("status").toString();
            
            // Kiểm tra xem user có tồn tại không
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Đảm bảo username khớp với userId
                if (!user.getUsername().equals(username)) {
                    response.put("success", false);
                    response.put("error", "Username không khớp với userId");
                    return response;
                }
                
                // Cập nhật trạng thái trong map onlineUsers
                if (status.equalsIgnoreCase("online") || status.equalsIgnoreCase("away")) {
                    onlineUsers.put(userId, status.toLowerCase());
                    System.out.println("[API] Cập nhật trạng thái: userId=" + userId + ", status=" + status);
                } else if (status.equalsIgnoreCase("offline")) {
                    onlineUsers.remove(userId);
                    System.out.println("[API] Xóa khỏi danh sách online: userId=" + userId);
                }
                
                // Broadcast trạng thái cho tất cả người dùng
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "USER_STATUS");
                notification.put("userId", userId);
                notification.put("username", username);
                notification.put("status", status);
                notification.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSend("/topic/user.status", notification);
                
                // Thành công
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("error", "Không tìm thấy người dùng với ID: " + userId);
            }
        } catch (Exception e) {
            System.err.println("[API] Lỗi khi cập nhật trạng thái người dùng: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    /**
     * Xử lý khi người dùng kết nối WebSocket thành công
     * Tự động đánh dấu họ là online
     */
    @EventListener
    public void handleWebSocketConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Kiểm tra sessionAttributes có null không trước khi truy cập
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            System.out.println("[WEBSOCKET_EVENT] Session attributes is null for session: " + sessionId);
            return;
        }
        
        // Lấy thông tin người dùng từ session attributes
        Object userIdObj = sessionAttributes.get("userId");
        Object usernameObj = sessionAttributes.get("username");
        
        if (userIdObj != null) {
            try {
                Long userId = Long.parseLong(userIdObj.toString());
                String username = usernameObj != null ? usernameObj.toString() : null;
                
                if (username == null) {
                    // Nếu không có username trong session, tìm trong database
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        username = userOpt.get().getUsername();
                    }
                }
                
                if (username != null) {
                    // Đánh dấu người dùng là online
                    onlineUsers.put(userId, "online");
                    System.out.println("[CONNECT] User connected: userId=" + userId + ", username=" + username);
                    
                    // Broadcast trạng thái online cho tất cả người dùng
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "USER_STATUS");
                    notification.put("userId", userId);
                    notification.put("username", username);
                    notification.put("status", "online");
                    notification.put("timestamp", System.currentTimeMillis());
                    
                    messagingTemplate.convertAndSend("/topic/user.status", notification);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Lỗi khi xử lý kết nối WebSocket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Xử lý khi người dùng ngắt kết nối WebSocket
     * Tự động đánh dấu họ là offline
     */
    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Kiểm tra sessionAttributes có null không trước khi truy cập
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            System.out.println("[WEBSOCKET_EVENT] Session attributes is null for session: " + sessionId);
            return;
        }
        
        // Lấy thông tin người dùng từ session attributes
        Object userIdObj = sessionAttributes.get("userId");
        Object usernameObj = sessionAttributes.get("username");
        
        if (userIdObj != null) {
            try {
                Long userId = Long.parseLong(userIdObj.toString());
                String username = usernameObj != null ? usernameObj.toString() : null;
                
                if (username == null) {
                    // Nếu không có username trong session, tìm trong database
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        username = userOpt.get().getUsername();
                    }
                }
                
                if (username != null) {
                    // Đánh dấu người dùng là offline
                    onlineUsers.remove(userId);
                    System.out.println("[DISCONNECT] User disconnected: userId=" + userId + ", username=" + username);
                    
                    // Broadcast trạng thái offline cho tất cả người dùng
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "USER_STATUS");
                    notification.put("userId", userId);
                    notification.put("username", username);
                    notification.put("status", "offline");
                    notification.put("timestamp", System.currentTimeMillis());
                    
                    messagingTemplate.convertAndSend("/topic/user.status", notification);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Lỗi khi xử lý ngắt kết nối WebSocket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
