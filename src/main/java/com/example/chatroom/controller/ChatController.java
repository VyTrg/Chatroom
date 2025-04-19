package com.example.chatroom.controller;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.service.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ChatController(ChatService chatService,
                          UserRepository userRepository,
                          ConversationRepository conversationRepository,
                          ConversationMemberRepository conversationMemberRepository,
                          ModelMapper modelMapper) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Tải tin nhắn với phân trang
     * @param page Số trang
     * @param size Kích thước trang
     * @return Danh sách tin nhắn
     */
    @MessageMapping("/chat.loadMessages/{page}/{size}")
    public List<ChatMessage> loadMessages(@DestinationVariable int page,
                                          @DestinationVariable int size) {
        return chatService.loadMessages(page, size);
    }

    //Gửi tin nhắn công khai
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Lấy thông tin người gửi từ username
        Optional<User> sender = userRepository.findByUsername(chatMessage.getSender());
        if (sender.isPresent()) {
            // Tạo entity Message để lưu vào DB
            Message message = new Message();
            message.setMessageText(chatMessage.getContent());
            message.setSender(sender.get());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);

            // Lưu tin nhắn vào DB
            chatService.saveMessage(message);
        }

        return chatMessage; // Trả về cho client để hiển thị
    }


// tin nhắn 1-1
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.private.{recipient}")
    @SendTo("/topic/private.{recipient}")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage,
                                          @DestinationVariable String recipient) {
        // Tìm người gửi và người nhận
        Optional<User> sender = userRepository.findByUsername(chatMessage.getSender());
        Optional<User> recipientUser = userRepository.findByUsername(recipient);

        if (sender.isPresent() && recipientUser.isPresent()) {
            // Tìm hoặc tạo cuộc hội thoại riêng tư giữa hai người
            Conversation conversation = chatService.findOrCreatePrivateConversation(
                    sender.get(), recipientUser.get());

            // Tạo entity Message để lưu
            Message message = new Message();
            message.setMessageText(chatMessage.getContent());
            message.setSender(sender.get());
            message.setConversation(conversation);
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);

            // Lưu tin nhắn vào DB
            chatService.saveMessage(message);
            // Gửi trực tiếp cho user nhận (dùng /user/queue/private)
            messagingTemplate.convertAndSendToUser(
                    recipient, "/queue/private", chatMessage
            );
        }
        System.out.println("Recipient: " + recipient);
        System.out.println("Sender: " + chatMessage.getSender());
        System.out.println("Gửi tới /user/" + recipient + "/queue/private");

        return chatMessage;
    }

//gửi tin nhắn nhóm
    @MessageMapping("/chat.group.{groupId}")
    @SendTo("/topic/group.{groupId}")
    public ChatMessage sendGroupMessage(@Payload ChatMessage chatMessage,
                                        @DestinationVariable Long groupId) {
        Optional<User> sender = userRepository.findByUsername(chatMessage.getSender());
        Optional<Conversation> conversation = conversationRepository.findById(groupId);

        if (sender.isPresent() && conversation.isPresent() && conversation.get().getIsGroup()) {
            // Kiểm tra quyền
            boolean isMember = conversationMemberRepository
                    .existsByUserIdAndConversationId(sender.get().getId(), groupId);
            if (!isMember) throw new RuntimeException("Bạn không thuộc nhóm này!");

            // Tạo entity Message để lưu
            Message message = new Message();
            message.setMessageText(chatMessage.getContent());
            message.setSender(sender.get());
            message.setConversation(conversation.get());
            message.setCreatedAt(LocalDateTime.now());
            message.setIsRead(false);

            // Lưu tin nhắn vào DB
            chatService.saveMessage(message);
        }
        return chatMessage;
    }

    /**
     * Thêm người dùng vào kênh công khai
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Lưu username trong session WebSocket
        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                .put("username", chatMessage.getSender());

        return chatMessage;
    }
}