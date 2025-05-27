package com.example.chatroom.service;

import com.example.chatroom.dto.ConversationWithRoleDTO;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return conversationRepository.findAllConversationsForUserWithRole(userId);
    }




}
