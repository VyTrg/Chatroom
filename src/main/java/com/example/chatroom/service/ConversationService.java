package com.example.chatroom.service;

import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;

import java.util.List;

public interface ConversationService {

    List<Conversation> getConversationsForUser(Long userId);

    void deleteConversation(Long conversationId);

    Conversation renameConversation(Long conversationId, String newName);

    List<ConversationMember> getConversationMembers(Long conversationId);
}
