package com.example.chatroom.controller;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.service.ChatService;
import com.example.chatroom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;

@Controller
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo({"/topic/public", "/topic/private"})
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        log.info("[PUBLIC] Nhận yêu cầu gửi public: {}", chatMessage);
        User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
        if (sender != null) {
            Message message = new Message();
            message.setSender(sender);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
        }
        log.info("[PUBLIC] Sending to /topic/public: {}", chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessage chatMessage) {
        log.info("[PRIVATE] Nhận yêu cầu gửi private: {}", chatMessage);
        User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
        if (sender == null) {
            log.error("[PRIVATE] Không tìm thấy sender: {}", chatMessage.getSender());
            return;
        }
        User recipient = null;
        if (chatMessage.getRecipientId() != null) {
            try {
                Long recipientId = Long.parseLong(chatMessage.getRecipientId());
                recipient = userRepository.findById(recipientId).orElse(null);
                if (recipient == null) {
                    log.error("[PRIVATE] Không tìm thấy recipient với recipientId: {}", recipientId);
                }
            } catch (Exception e) {
                log.error("[PRIVATE] recipientId không hợp lệ: {}", chatMessage.getRecipientId(), e);
            }
        } else {
            log.error("[PRIVATE] recipientId bị null!");
        }
        // NGĂN KHÔNG CHO TỰ NHẮN CHO CHÍNH MÌNH
        if (sender != null && recipient != null) {
            if (sender.getId().equals(recipient.getId())) {
                log.warn("[PRIVATE] Người gửi và người nhận là một! Không gửi tin nhắn cho chính mình.");
                return;
            }
            log.info("[PRIVATE] Sender: {} (id={}), Recipient: {} (id={})", sender.getUsername(), sender.getId(), recipient.getUsername(), recipient.getId());
            Conversation conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
            Message message = new Message();
            message.setSender(sender);
            message.setConversation(conversation);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
        }
        String recipientUsername = (recipient != null) ? recipient.getUsername() : chatMessage.getRecipientId();
        log.info("[PRIVATE] Sending from {} to {} (recipientUsername) với content: {}", chatMessage.getSender(), recipientUsername, chatMessage.getContent());
        messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/private", chatMessage);
    }

    @MessageMapping("/chat.group")
    public void sendGroup(@Payload ChatMessage chatMessage) {
        log.info("[GROUP] Nhận yêu cầu gửi group: {}", chatMessage);
        try {
            Long groupId = chatMessage.getGroupId();
            Conversation group = chatService.getGroupConversationById(groupId);
            User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
            if (group == null) {
                log.error("[GROUP] Không tìm thấy group với groupId: {}", groupId);
                return;
            }
            if (sender == null) {
                log.error("[GROUP] Không tìm thấy sender: {}", chatMessage.getSender());
                return;
            }
            // Kiểm tra sender có phải thành viên group không
            if (!chatService.isUserInGroup(sender, group)) {
                log.error("[GROUP] Sender {} không phải thành viên group {}", sender.getUsername(), groupId);
                return;
            }
            Message message = new Message();
            message.setSender(sender);
            message.setConversation(group);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
            log.info("[GROUP] Đã lưu message vào group {}: {}", groupId, chatMessage.getContent());
            log.info("[GROUP] Sending to /topic/group.{}: {}", groupId, chatMessage);
            messagingTemplate.convertAndSend("/topic/group." + groupId, chatMessage);
        } catch (Exception e) {
            log.error("[GROUP] recipient (groupId) không hợp lệ: {}", chatMessage.getGroupId(), e);
        }
    }

    // Nhận tin nhắn từ client (REST API)
    @PostMapping("/receive-message")
    @ResponseBody
    public ResponseEntity<String> receiveMessage(@RequestBody ChatMessage chatMessage) {
        try {
            User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
            if (sender == null) {
                return ResponseEntity.badRequest().body("Người gửi không tồn tại");
            }
            Message message = new Message();
            message.setSender(sender);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
            // Có thể gửi tin nhắn tới client nếu muốn
            // messagingTemplate.convertAndSend("/topic/public", chatMessage);
            return ResponseEntity.ok("Nhận tin nhắn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi nhận tin nhắn: " + e.getMessage());
        }
    }
}