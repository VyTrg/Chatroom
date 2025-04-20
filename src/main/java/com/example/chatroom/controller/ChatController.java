package com.example.chatroom.controller;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.service.ChatService;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
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
        if (sender != null) {
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
        User recipient = userRepository.findByUsername(chatMessage.getRecipient()).orElse(null);
        if (sender != null && recipient != null) {
            Conversation conversation = chatService.findOrCreatePrivateConversation(sender, recipient);
            Message message = new Message();
            message.setSender(sender);
            message.setConversation(conversation);
            message.setMessageText(chatMessage.getContent());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);
            chatService.saveMessage(message);
        }
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/private", chatMessage);
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
}