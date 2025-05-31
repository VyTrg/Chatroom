package com.example.chatroom.controller;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.chat.MessageType;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.repository.ConversationRepository;
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
import java.util.Optional;

@Controller
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserRepository userRepository, ConversationRepository conversationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
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

//    @MessageMapping("/chat.private")
//    public void sendPrivate(@Payload ChatMessage chatMessage) {
//        User sender = userRepository.findByUsername(chatMessage.getSender()).orElse(null);
//        User recipient = null;
//        if (chatMessage.getRecipientId() != null) {
//            try {
//                Long recipientId = Long.parseLong(chatMessage.getRecipientId());
//                recipient = userRepository.findById(recipientId).orElse(null);
//            } catch (Exception e) {
//                log.error("[PRIVATE] Error parsing recipient ID: {}", e.getMessage(), e);
//                recipient = null;
//            }
//        }
//        if (sender != null && recipient != null) {
//            try {
//                // Log thông tin người gửi và người nhận để debug
//                log.info("[PRIVATE] Sending from {} (ID={}) to {} (ID={})",
//                        sender.getUsername(), sender.getId(),
//                        recipient.getUsername(), recipient.getId());
//
//                // Lưu tin nhắn vào database
//                Conversation conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
//                log.info("[PRIVATE] Using conversation ID: {}", conversation.getId());
//
//                Message message = new Message();
//                message.setSender(sender);
//                message.setConversation(conversation);
//                message.setMessageText(chatMessage.getContent());
//                message.setCreatedAt(LocalDateTime.now());
//                message.setIsRead(false);
//                Message savedMessage = chatService.saveMessage(message);
//
//                // Thêm thông tin đầy đủ vào chatMessage để gửi qua WebSocket
//                chatMessage.setMessageId(savedMessage.getId().toString());
//                chatMessage.setConversationId(conversation.getId().toString());
//                chatMessage.setType(MessageType.CHAT);
//                chatMessage.setTimestamp(savedMessage.getCreatedAt().toString());
//                chatMessage.setSenderId(sender.getId().toString());
//
//                // Gửi tin nhắn qua topic của cuộc trò chuyện
//                String conversationTopic = "/topic/private." + conversation.getId();
//                log.info("[PRIVATE] Sending to topic: {} with content: {}", conversationTopic, chatMessage.getContent());
//                messagingTemplate.convertAndSend(conversationTopic, chatMessage);
//
//                // Gửi tin nhắn riêng trực tiếp đến người nhận
//                log.info("[PRIVATE] Sending direct message to recipient: {}", recipient.getId());
//                messagingTemplate.convertAndSendToUser(
//                        recipient.getId().toString(),
//                        "/queue/messages",
//                        chatMessage
//                );
//
//                // Gửi tin nhắn riêng trực tiếp đến người gửi
//                log.info("[PRIVATE] Also sending direct message to sender: {}", sender.getId());
//                messagingTemplate.convertAndSendToUser(
//                        sender.getId().toString(),
//                        "/queue/messages",
//                        chatMessage
//                );
//
//                log.info("[PRIVATE] Message saved with ID: {} and sent successfully", savedMessage.getId());
//            } catch (Exception e) {
//                log.error("[PRIVATE] Error sending private message: {}", e.getMessage(), e);
//            }
//        } else {
//            log.error("[PRIVATE] Failed to send message - sender or recipient not found. Sender: {}, Recipient ID: {}",
//                    chatMessage.getSender(), chatMessage.getRecipientId());
//        }
//    }

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
                // Log thông tin người gửi và người nhận để debug
                log.info("[PRIVATE] Sending from {} (ID={}) to {} (ID={})",
                        sender.getUsername(), sender.getId(),
                        recipient.getUsername(), recipient.getId());

                // Kiểm tra conversationId từ frontend
                Conversation conversation;
                String frontendConversationId = chatMessage.getConversationId();

                if (frontendConversationId != null && !frontendConversationId.isEmpty()) {
                    try {
                        log.info("[PRIVATE] Frontend provided conversation ID: {}", frontendConversationId);
                        Long conversationId = Long.parseLong(frontendConversationId);

                        // Tìm cuộc trò chuyện theo ID
                        Optional<Conversation> existingConversation = conversationRepository.findById(conversationId);

                        if (existingConversation.isPresent()) {
                            // Kiểm tra xem cả hai người dùng có trong cuộc trò chuyện không
                            Conversation foundConversation = existingConversation.get();
                            boolean senderInConv = chatService.isUserInConversation(sender, foundConversation);
                            boolean recipientInConv = chatService.isUserInConversation(recipient, foundConversation);

                            if (senderInConv && recipientInConv) {
                                // Sử dụng cuộc trò chuyện hiện có vì cả hai người dùng đều có trong đó
                                log.info("[PRIVATE] Using existing conversation with ID: {}", conversationId);
                                conversation = foundConversation;
                            } else {
                                // Một trong hai người dùng không có trong cuộc trò chuyện này
                                log.warn("[PRIVATE] Users not found in conversation {}, creating new one", conversationId);
                                conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
                            }
                        } else {
                            // Không tìm thấy cuộc trò chuyện với ID đã cho
                            log.info("[PRIVATE] Conversation {} not found, creating new one", conversationId);
                            conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
                        }
                    } catch (NumberFormatException e) {
                        // ID không phải là số hợp lệ
                        log.warn("[PRIVATE] Invalid conversation ID format: {}", frontendConversationId);
                        conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
                    }
                } else {
                    // Không có conversationId từ frontend
                    log.info("[PRIVATE] No conversation ID provided, finding or creating one");
                    conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
                }

                log.info("[PRIVATE] Using conversation ID: {}", conversation.getId());
                // KẾT THÚC thay đổi

                Message message = new Message();
                message.setSender(sender);
                message.setConversation(conversation);
                message.setMessageText(chatMessage.getContent());
                message.setCreatedAt(LocalDateTime.now());
                message.setIsRead(false);
                Message savedMessage = chatService.saveMessage(message);

                // Thêm thông tin đầy đủ vào chatMessage để gửi qua WebSocket
                chatMessage.setMessageId(savedMessage.getId().toString());
                chatMessage.setConversationId(conversation.getId().toString());
                chatMessage.setType(MessageType.CHAT);
                chatMessage.setTimestamp(savedMessage.getCreatedAt().toString());
                chatMessage.setSenderId(sender.getId().toString());

                // Gửi tin nhắn qua topic của cuộc trò chuyện
                String conversationTopic = "/topic/private." + conversation.getId();
                log.info("[PRIVATE] Sending to topic: {} with content: {}", conversationTopic, chatMessage.getContent());
                messagingTemplate.convertAndSend(conversationTopic, chatMessage);

                // Gửi tin nhắn riêng trực tiếp đến người nhận
                log.info("[PRIVATE] Sending direct message to recipient: {}", recipient.getId());
                messagingTemplate.convertAndSendToUser(
                        recipient.getId().toString(),
                        "/queue/messages",
                        chatMessage
                );

                // Gửi tin nhắn riêng trực tiếp đến người gửi
                log.info("[PRIVATE] Also sending direct message to sender: {}", sender.getId());
                messagingTemplate.convertAndSendToUser(
                        sender.getId().toString(),
                        "/queue/messages",
                        chatMessage
                );

                log.info("[PRIVATE] Message saved with ID: {} and sent successfully", savedMessage.getId());
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
                chatMessage.setType(MessageType.CHAT);
                chatMessage.setTimestamp(savedMessage.getCreatedAt().toString());

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