package com.example.chatroom.service;

import com.example.chatroom.model.Message;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;


    @Override
    public List<Message> getUnreadMessagesForUserInConversation(Long userId, Long conversationId) {
        return messageRepository.findUnreadMessagesForUserInConversation(userId, conversationId);
    }

    @Override
    public List<Message> getLatestMessagesForUser(Long userId) {
        return messageRepository.findLatestMessagesForUser(userId);
    }

    @Override
    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findMessagesByConversationId(conversationId);
    }


}
