package com.example.chatroom.service;

import com.example.chatroom.model.Conversation;
import com.example.chatroom.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConservationServiceImpl implements ConservationService {
    @Autowired
    private ConversationRepository conversationRepository;


    @Override
    public List<Conversation> getAllConversationsForUser(Long userId) {
        return conversationRepository.findAllConversationsForUser(userId);
    }
}
