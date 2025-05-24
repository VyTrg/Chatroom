package com.example.chatroom.service;

import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
    
    @Autowired
    private MessageService messageService;

    @Override
    public List<Conversation> getConversationsForUser(Long userId) {
        return conversationRepository.findAllConversationsForUser(userId);
    }

    @Override
    public void deleteConversation(Long conversationId) {
        // 1. Xóa tất cả tin nhắn trong cuộc trò chuyện
        messageService.deleteAllMessagesInConversation(conversationId);
        
        // 2. Xóa cuộc trò chuyện
        conversationRepository.deleteById(conversationId);
    }

    @Override
    public Conversation renameConversation(Long conversationId, String newName) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found with id: " + conversationId));
        
        conversation.setName(newName);
        return conversationRepository.save(conversation);
    }

    @Override
    public List<ConversationMember> getConversationMembers(Long conversationId) {
        return conversationMemberRepository.findAllByConversationId(conversationId);
    }
}
