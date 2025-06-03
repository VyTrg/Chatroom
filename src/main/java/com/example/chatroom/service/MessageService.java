package com.example.chatroom.service;

import com.example.chatroom.model.Message;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageService {
    List<Message> getUnreadMessagesForUserInConversation(Long userId, Long conversationId);

    List<Message> getLatestMessagesForUser(Long userId);

    List<Message> getMessagesByConversation(Long conversationId);
    
    void deleteMessage(Long messageId);
    
    void deleteAllMessagesInConversation(Long conversationId);
    
    Message updateMessage(Long messageId, String newContent);
    
    void deleteAllMessagesByConversationId(Long conversationId);
    
    Message markMessageAsRead(Long messageId);
    
    List<Message> markAllMessagesAsReadInConversation(Long conversationId, Long userId);
}
