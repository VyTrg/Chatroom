package com.example.chatroom.controller;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.model.ConversationMember;
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
                log.error("[PRIVATE] Error parsing recipient ID: {}", e.getMessage(), e);
                recipient = null;
            }
        }
        if (sender != null && recipient != null) {
            try {
                // Lưu tin nhắn vào database
                Conversation conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
                Message message = new Message();
                message.setSender(sender);
                message.setConversation(conversation);
                message.setMessageText(chatMessage.getContent());
                message.setCreatedAt(LocalDateTime.now());
                message.setIsRead(false);
                Message savedMessage = chatService.saveMessage(message);
                
                // Add the message ID and conversation ID to the chat message
                chatMessage.setMessageId(savedMessage.getId().toString());
                chatMessage.setConversationId(conversation.getId().toString());
                
//                // Send message to recipient
//                String recipientUsername = recipient.getUsername();
//                System.out.println("[PRIVATE] Sending from " + chatMessage.getSender() + " to " + recipientUsername);
//                messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/private", chatMessage);

                String conversationTopic = "/topic/private." + conversation.getId();
                System.out.println("[PRIVATE] Sending to topic: " + conversationTopic);

                messagingTemplate.convertAndSend(conversationTopic, chatMessage);


//                // Also send a copy to the sender so they can see their own messages
//                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", chatMessage);
                
                log.info("[PRIVATE] Message saved with ID: {} and sent from {} to {} with content: {}", 
                        savedMessage.getId(),
                        chatMessage.getSender(), 
//                        recipientUsername,
                        chatMessage.getSender(),
                        chatMessage.getContent());
            } catch (Exception e) {
                log.error("[PRIVATE] Error sending private message: {}", e.getMessage(), e);
            }
        } else {
            log.error("[PRIVATE] Failed to send message - sender or recipient not found. Sender: {}, Recipient ID: {}", 
                    chatMessage.getSender(), chatMessage.getRecipientId());
        }
    }

    @MessageMapping("/chat.group")
    public void sendGroup(@Payload ChatMessage chatMessage) {
        // groupId truyền qua recipient
        try {
//            Long groupId = Long.parseLong(chatMessage.getRecipient());
            Long groupId = chatMessage.getGroupId();
            Conversation group = chatService.getGroupConversationById(groupId);
            User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
            if (group != null && sender != null) {
                Message message = new Message();
                message.setSender(sender);
                message.setConversation(group);
                message.setMessageText(chatMessage.getContent());
                message.setCreatedAt(LocalDateTime.now());
                message.setIsRead(false);
                Message savedMessage = chatService.saveMessage(message);
                
                // Add the message ID and conversation ID to the chat message
                chatMessage.setMessageId(savedMessage.getId().toString());
                chatMessage.setConversationId(group.getId().toString());
                
                log.info("[GROUP] Message saved with ID: {} and sent to group {} from {}", 
                         savedMessage.getId(), groupId, chatMessage.getSender());
                
                messagingTemplate.convertAndSend("/topic/group." + groupId, chatMessage);
            } else {
                log.error("[GROUP] Failed to send message - group or sender not found. Group ID: {}, Sender: {}", 
                          groupId, chatMessage.getSender());
            }
        } catch (Exception e) {
            log.error("[GROUP] Error sending group message: {}", e.getMessage(), e);
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