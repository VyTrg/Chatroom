package com.example.chatroom.service;

import com.example.chatroom.model.Message;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AttachmentService attachmentService;

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
        try {

            
            // Sau đó xóa tin nhắn
            messageRepository.deleteById(messageId);
            System.out.println("Đã xóa tin nhắn ID: " + messageId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tin nhắn và tệp đính kèm: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteAllMessagesInConversation(Long conversationId) {
        // Xóa tất cả tin nhắn trong cuộc trò chuyện
        List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
        messageRepository.deleteAll(messages);
    }
    
    @Override
    public void deleteAllMessagesByConversationId(Long conversationId) {
        try {

            // Sau đó xóa tất cả tin nhắn trong cuộc trò chuyện
            List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
            if (messages != null && !messages.isEmpty()) {
                System.out.println("Xóa " + messages.size() + " tin nhắn từ cuộc trò chuyện " + conversationId);
                messageRepository.deleteAll(messages);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tin nhắn và tệp đính kèm: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
    
    @Override
    public Message markMessageAsRead(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setIsRead(true);
            return messageRepository.save(message);
        } else {
            throw new NoSuchElementException("Không tìm thấy tin nhắn với ID: " + messageId);
        }
    }
    
    @Override
    public List<Message> markAllMessagesAsReadInConversation(Long conversationId, Long userId) {
        // Lấy tất cả tin nhắn chưa đọc trong cuộc trò chuyện không phải do người dùng hiện tại gửi
        List<Message> unreadMessages = messageRepository.findUnreadMessagesForUserInConversation(userId, conversationId);
        
        // Đánh dấu tất cả là đã đọc
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
        
        return unreadMessages;
    }
}
