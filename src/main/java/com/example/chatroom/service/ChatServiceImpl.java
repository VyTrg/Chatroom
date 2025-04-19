
package com.example.chatroom.service;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.chat.MessageType;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    // Giữ lại bộ nhớ tạm của các ChatMessage cho phương thức hiện tại
    private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());

    // Thêm các repository cần thiết nếu đã tồn tại
    private MessageRepository messageRepository;
    private ConversationRepository conversationRepository;
    private ConversationMemberRepository conversationMemberRepository;
    private ModelMapper modelMapper;

    // Constructor với các phụ thuộc tùy chọn
    @Autowired
    public ChatServiceImpl(
            // Nếu các repository chưa có, bạn có thể tạm thời comment chúng lại
            Optional<MessageRepository> messageRepository,
            Optional<ConversationRepository> conversationRepository,
            Optional<ConversationMemberRepository> conversationMemberRepository,
            Optional<ModelMapper> modelMapper) {
        this.messageRepository = messageRepository.orElse(null);
        this.conversationRepository = conversationRepository.orElse(null);
        this.conversationMemberRepository = conversationMemberRepository.orElse(null);
        this.modelMapper = modelMapper.orElse(null);
    }

    @Override
    public List<ChatMessage> loadMessages() {
        // Giữ lại triển khai hiện tại
        return new ArrayList<>(messages);
    }

    @Override
    public List<ChatMessage> loadMessages(int page, int size) {
        return List.of();
    }

    @Override
    public List<ChatMessage> loadMessages(Long conversationId, Long userId, int page, int size) {
        // Triển khai mới với phân trang
        if (messageRepository == null) {
            // Nếu chưa cấu hình repository, sử dụng danh sách trong bộ nhớ
            int start = page * size;
            int end = Math.min(start + size, messages.size());
            return new ArrayList<>(messages.subList(start, end));
        }
        validateUserInConversation(conversationId, userId);
        // Nếu đã cấu hình repository
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Message> messageEntities = messageRepository.findAll(pageable).getContent();

        return messageEntities.stream()
                .map(message -> {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setContent(message.getMessageText());
                    chatMessage.setSender(message.getSender().getUsername());
                    chatMessage.setType(MessageType.CHAT);
                    return chatMessage;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addMessage(ChatMessage message) {
        // Giữ lại triển khai hiện tại
        messages.add(message);
    }
    @Override
    public void validateUserInConversation(Long conversationId, Long userId) {
        if (!conversationMemberRepository.existsByUserIdAndConversationId(userId, conversationId)) {
            throw new RuntimeException("User không thuộc cuộc hội thoại này");
        }
    }

    @Override
    @Transactional
    public Message saveMessage(Message message) {
        if (messageRepository == null) {
            throw new IllegalStateException("MessageRepository chưa được cấu hình");
        }
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Conversation findOrCreatePrivateConversation(User user1, User user2) {
        if (conversationRepository == null || conversationMemberRepository == null) {
            throw new IllegalStateException("Repository chưa được cấu hình đầy đủ");
        }

        // Tìm cuộc hội thoại riêng tư giữa hai người dùng
        Optional<Conversation> existingConversation = conversationRepository
                .findPrivateConversationBetween(user1.getId(), user2.getId());

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        // Tạo cuộc hội thoại mới
        Conversation newConversation = new Conversation();
        newConversation.setName(user1.getUsername() + " & " + user2.getUsername());
        newConversation.setIsGroup(false);
        newConversation.setCreatedAt(LocalDateTime.now());

        Conversation savedConversation = conversationRepository.save(newConversation);

        // Thêm thành viên
        ConversationMember member1 = new ConversationMember();
        member1.setConversation(savedConversation);
        member1.setUser(user1);
        conversationMemberRepository.save(member1);

        ConversationMember member2 = new ConversationMember();
        member2.setConversation(savedConversation);
        member2.setUser(user2);
        conversationMemberRepository.save(member2);

        return savedConversation;
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        if (messageRepository == null) {
            throw new IllegalStateException("MessageRepository chưa được cấu hình");
        }

        Optional<Message> messageOpt = messageRepository.findById(messageId);

        messageOpt.ifPresent(message -> {
            // Kiểm tra người nhận không phải người gửi
            if (!message.getSender().getId().equals(userId)) {
                message.setIsRead(true);
                message.setUpdatedAt(LocalDateTime.now());
                messageRepository.save(message);
            }
        });
    }
}