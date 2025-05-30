package com.example.chatroom.service;

import com.example.chatroom.model.Message;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDateTime;

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

    @Override
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public void deleteAllMessagesInConversation(Long conversationId) {
        // Xóa tất cả tin nhắn trong cuộc trò chuyện
        List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
        messageRepository.deleteAll(messages);
    }
    
    @Override
    public void deleteAllMessagesByConversationId(Long conversationId) {
        // Xóa tất cả tin nhắn trong cuộc trò chuyện
        List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
        messageRepository.deleteAll(messages);
    }
    
    @Override
    public Message updateMessage(Long messageId, String newContent) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setMessageText(newContent);
            message.setUpdatedAt(LocalDateTime.now());  // Cập nhật thởi gian chỉnh sửa
            return messageRepository.save(message);
        } else {
            throw new NoSuchElementException("Không tìm thấy tin nhắn với ID: " + messageId);
        }
    }
}
