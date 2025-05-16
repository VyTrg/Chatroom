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
import com.example.chatroom.repository.UserRepository;
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
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    // Constructor với các phụ thuộc tùy chọn
    @Autowired
    public ChatServiceImpl(
            // Nếu các repository chưa có, bạn có thể tạm thói comment chúng lại
            Optional<MessageRepository> messageRepository,
            Optional<ConversationRepository> conversationRepository,
            Optional<ConversationMemberRepository> conversationMemberRepository,
            Optional<UserRepository> userRepository,
            Optional<ModelMapper> modelMapper) {
        this.messageRepository = messageRepository.orElse(null);
        this.conversationRepository = conversationRepository.orElse(null);
        this.conversationMemberRepository = conversationMemberRepository.orElse(null);
        this.userRepository = userRepository.orElse(null);
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
        System.out.println("=== [CHAT_SERVICE] Tìm hoặc tạo cuộc hội thoại riêng tư ===");
        System.out.println("[CHAT_SERVICE] User1 ID: " + user1.getId() + ", username: " + user1.getUsername());
        System.out.println("[CHAT_SERVICE] User2 ID: " + user2.getId() + ", username: " + user2.getUsername());

        if (conversationRepository == null || conversationMemberRepository == null) {
            System.err.println("[CHAT_SERVICE] Lỗi: Repository chưa được cấu hình đầy đủ");
            throw new IllegalStateException("Repository chưa được cấu hình đầy đủ");
        }
        try {
            Optional<Conversation> existingConversation = conversationRepository
                    .findPrivateConversationBetween(user1.getId(), user2.getId());

            if (existingConversation.isPresent()) {
                System.out.println("[CHAT_SERVICE] Đã tìm thấy cuộc hội thoại: " + existingConversation.get().getId());
                return existingConversation.get();
            }

            // Lấy lại User từ database để đảm bảo managed entity
            User managedUser1 = userRepository.findById(user1.getId()).orElseThrow();
            User managedUser2 = userRepository.findById(user2.getId()).orElseThrow();

            // Tạo cuộc hội thoại mới
            System.out.println("[CHAT_SERVICE] Không tìm thấy cuộc hội thoại, tạo mới");
            Conversation conversation = new Conversation();
            conversation.setIsGroup(false);
            conversation.setCreatedAt(LocalDateTime.now());
            // Đặt tên cuộc hội thoại (bắt buộc, không được null)
            conversation.setName(managedUser1.getUsername() + " & " + managedUser2.getUsername());
            Conversation savedConversation = conversationRepository.save(conversation);
            System.out.println("[CHAT_SERVICE] Đã tạo cuộc hội thoại mới với ID: " + savedConversation.getId());

            // Thêm thành viên vào cuộc hội thoại (phải dùng managed entity)
            System.out.println("[CHAT_SERVICE] Thêm user1 vào cuộc hội thoại");
            ConversationMember member1 = new ConversationMember();
            member1.setConversation(savedConversation);
            member1.setUser(managedUser1);
            member1.setRole("member");
            conversationMemberRepository.save(member1);

            System.out.println("[CHAT_SERVICE] Thêm user2 vào cuộc hội thoại");
            ConversationMember member2 = new ConversationMember();
            member2.setConversation(savedConversation);
            member2.setUser(managedUser2);
            member2.setRole("member");
            conversationMemberRepository.save(member2);

            System.out.println("[CHAT_SERVICE] Hoàn tất tạo cuộc hội thoại và thêm thành viên");
            return savedConversation;
        } catch (Exception e) {
            System.err.println("[CHAT_SERVICE] Lỗi khi tìm/tạo cuộc hội thoại: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

    @Override
    public Conversation getGroupConversationById(Long groupId) {
        return conversationRepository.findById(groupId).orElse(null);
    }

    @Override
    public List<ConversationMember> getGroupMembers(Long groupId) {
        return conversationMemberRepository.findAllByConversationId(groupId);
    }

    @Override
    public boolean isUserInGroup(User user, Conversation group) {
        if (user == null || group == null) {
            System.out.println("[isUserInGroup] user or group is null! user=" + user + ", group=" + group);
            return false;
        }
        boolean result = conversationMemberRepository.existsByUserIdAndConversationId(user.getId(), group.getId());
        System.out.println("[isUserInGroup] userId=" + user.getId() + ", groupId=" + group.getId() + ", exists=" + result);
        return result;
    }

    @Override
    public List<Message> getMessagesByConversationId(Long conversationId) {
        if (messageRepository == null) {
            // Nếu repository chưa được cấu hình, trả về rỗng
            return new ArrayList<>();
        }
        // Lấy tất cả tin nhắn theo conversationId, sắp xếp theo thời gian tăng dần
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, Pageable.unpaged()).getContent();
    }
}