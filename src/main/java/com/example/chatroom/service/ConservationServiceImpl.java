package com.example.chatroom.service;

import com.example.chatroom.dto.ConversationWithRoleDTO;
import com.example.chatroom.model.*;
import com.example.chatroom.repository.*;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ConservationServiceImpl implements ConservationService {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactWithRepository contactWithRepository;
    
    private EntityManager entityManager;
    
    @Autowired
    private BlockRepository blockRepository;

    @Override
    public List<Conversation> getAllConversationsForUser(Long userId) {
        return conversationRepository.findAllConversationsForUser(userId);
    }

    @Override
    public Conversation createGroupConversation(List<Long> userIds, String conversationName) {
        if (userIds == null || userIds.size() < 3) {
            throw new IllegalArgumentException("At least 3 user IDs are required");
        }

        String finalConversationName = conversationName != null && !conversationName.trim().isEmpty() ? conversationName : "new group";
        if (finalConversationName.equals("new group")) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < userIds.size(); i++) {
                User user = userRepository.findById(userIds.get(i))
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                nameBuilder.append(user.getFirstName()).append(" ").append(user.getLastName());
                if (i < userIds.size() - 1) {
                    nameBuilder.append(", ");
                }
            }
            finalConversationName = nameBuilder.toString();
        }

        Conversation conversation = new Conversation();
        conversation.setName(finalConversationName);
        conversation.setIsGroup(true);
        conversation.setImageUrl(null);
        conversation = conversationRepository.saveAndFlush(conversation);

        List<ConversationMember> members = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            User user = userRepository.findById(userIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            ConversationMember member = new ConversationMember();
            member.setConversation(conversation);
            member.setUser(user);
            member.setRole(i == 0 ? "admin" : "member");
            members.add(conversationMemberRepository.saveAndFlush(member));
        }


        return conversation;
    }

    @Override
    public List<ConversationWithRoleDTO> getAllConversationsForUserWithRole(Long userId) {
        // Log bắt đầu truy vấn
        log.info("Bắt đầu tìm cuộc trò chuyện cho user: {}", userId);

        try {
            // Sử dụng phương pháp thủ công để xây dựng kết quả
            List<Conversation> conversations = conversationRepository.findSimpleConversationsForUser(userId);
            log.info("Tìm thấy {} cuộc trò chuyện cơ bản", conversations.size());
            
            List<ConversationWithRoleDTO> result = new ArrayList<>();

            for (Conversation conversation : conversations) {
                try {
                    // Lấy thông tin thành viên của người dùng hiện tại
                    Optional<ConversationMember> currentMemberOpt = conversationMemberRepository.findByUserIdAndConversationId(
                            userId, conversation.getId());
                    
                    if (currentMemberOpt.isEmpty()) {
                        log.warn("Không tìm thấy thành viên cho userId={} trong cuộc trò chuyện {}",
                                userId, conversation.getId());
                        continue;
                    }
                    
                    ConversationMember currentMember = currentMemberOpt.get();
                    String role = currentMember.getRole();

                    Long otherUserId = null;
                    LocalDateTime contactDeletedAt = null;
                    Long blockerId = null;

                    // Chỉ lấy thông tin người dùng khác nếu không phải nhóm
                    if (!conversation.getIsGroup()) {
                        try {
                            // Tìm thành viên khác trong cuộc trò chuyện 1-1
                            List<User> allMembers = conversationMemberRepository.findAllMembersInConversation(conversation.getId());
                            
                            User otherUser = null;
                            for (User user : allMembers) {
                                if (!user.getId().equals(userId)) {
                                    otherUser = user;
                                    break;
                                }
                            }

                            if (otherUser != null) {
                                otherUserId = otherUser.getId();
                                log.debug("Tìm thấy người dùng khác: {}", otherUserId);

                                // Tìm thông tin liên hệ
                                List<ContactWith> contactOpt = contactWithRepository.findContactBetweenUsers(
                                        userId, otherUserId);
                                
                                if (!contactOpt.isEmpty()) {
                                    // Lấy contact mới nhất
                                    contactOpt.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
                                    ContactWith contact = contactOpt.get(0);
                                    contactDeletedAt = contact.getDeletedAt();
                                    log.debug("Contact deletedAt: {}", contactDeletedAt);
                                }

                                // Tìm thông tin chặn
                                Optional<Block> blockOpt = blockRepository.findBlockBetweenUsers(userId, otherUserId);
                                if (blockOpt.isPresent()) {
                                    blockerId = blockOpt.get().getBlocker().getId();
                                    log.debug("Blocker ID: {}", blockerId);
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Lỗi khi xử lý thông tin người dùng khác: {}", e.getMessage());
                        }
                    }

                    // Tạo DTO với đầy đủ thông tin
                    ConversationWithRoleDTO dto = new ConversationWithRoleDTO(
                            conversation.getId(),
                            conversation.getName(),
                            conversation.getIsGroup(),
                            conversation.getImageUrl(),
                            conversation.getCreatedAt(),
                            conversation.getDeletedAt(),
                            contactDeletedAt,
                            role,
                            otherUserId,
                            blockerId,
                            userId
                    );

                    result.add(dto);
                    log.debug("Đã xử lý cuộc trò chuyện: {}", conversation.getId());
                } catch (Exception e) {
                    log.error("Lỗi khi xử lý cuộc trò chuyện {}: {}", conversation.getId(), e.getMessage());
                }
            }

            log.info("Đã xử lý thành công {} cuộc trò chuyện", result.size());
            return result;

        } catch (Exception e) {
            log.error("Lỗi khi tìm cuộc trò chuyện cho user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}
