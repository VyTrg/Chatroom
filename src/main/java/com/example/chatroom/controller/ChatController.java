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
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Lưu tin nhắn public vào DB nếu muốn
        User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
        if (sender != null && chatMessage.getContent() != null && !chatMessage.getContent().trim().isEmpty()) {
            Message message = new Message();
            message.setSender(sender);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
        }
        return chatMessage;
    }

    @MessageMapping("/chat.private")
    public void sendPrivate(@Payload ChatMessage chatMessage) {
        User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
        User recipient = null;
        if (chatMessage.getRecipientId() != null) {
            try {
                Long recipientId = Long.parseLong(chatMessage.getRecipientId());
                recipient = userRepository.findById(recipientId).orElse(null);
            } catch (Exception e) {
                recipient = null;
            }
        }
        if (sender != null && recipient != null) {
            Conversation conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
            Message message = new Message();
            message.setSender(sender);
            message.setConversation(conversation);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
//            chatService.saveMessage(message);
        }
        String recipientUsername = (recipient != null) ? recipient.getUsername() : chatMessage.getRecipient();
        System.out.println("[PRIVATE] Sending from " + chatMessage.getSender() + " to " + recipientUsername);
        log.info("[PRIVATE] Sending from {} to {} with content: {}",
                chatMessage.getSender(),
                recipientUsername,
                chatMessage.getContent());
        messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/private", chatMessage);
    }

    @MessageMapping("/chat.group")
    public void sendGroup(@Payload ChatMessage chatMessage) {
        // groupId truyền qua recipient
        try {
            Long groupId = Long.parseLong(chatMessage.getRecipient());
            Conversation group = chatService.getGroupConversationById(groupId);
            User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
            if (group != null && sender != null) {
                Message message = new Message();
                message.setSender(sender);
                message.setConversation(group);
                message.setMessageText(chatMessage.getContent());
                message.setCreatedAt(LocalDateTime.now());
                message.setIsRead(false);
                chatService.saveMessage(message);
            }
            messagingTemplate.convertAndSend("/topic/group." + groupId, chatMessage);
        } catch (Exception e) {
            // log lỗi nếu recipient không phải là groupId hợp lệ
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