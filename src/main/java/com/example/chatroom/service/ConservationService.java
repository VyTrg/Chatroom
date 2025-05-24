package com.example.chatroom.service;

import com.example.chatroom.model.Conversation;

import java.util.List;

public interface ConservationService {
    List<Conversation> getAllConversationsForUser(Long userId);

    Conversation createGroupConversation(List<Long> userIds, String conversationName);

}
