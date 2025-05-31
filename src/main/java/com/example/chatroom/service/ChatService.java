package com.example.chatroom.service;

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;

import java.util.List;

public interface ChatService {
    /**
     * Tải tất cả tin nhắn
     */
    List<ChatMessage> loadMessages();

    /**
     * Tải tin nhắn có phân trang
     * @param page Số trang
     * @param size Kích thước trang
     */
    List<ChatMessage> loadMessages(int page, int size);

    List<ChatMessage> loadMessages(Long conversationId, Long userId, int page, int size);

    /**
     * Thêm tin nhắn DTO vào bộ nhớ tạm
     */
    void addMessage(ChatMessage message);

    void validateUserInConversation(Long conversationId, Long userId);

    /**
     * Lưu tin nhắn entity vào database
     */
    Message saveMessage(Message message);

    /**
     * Tìm hoặc tạo cuộc hội thoại riêng tư
     */
    Conversation findOrCreatePrivateConversation(User user1, User user2);

    Conversation getGroupConversationById(Long groupId);

    List<ConversationMember> getGroupMembers(Long groupId);

    /**
     * Kiểm tra user có phải thành viên group không
     */
    boolean isUserInGroup(User user, Conversation group);

    boolean isUserInConversation(User user, Conversation conversation);

//     Đánh dấu tin nhắn đã đọc
    void markMessageAsRead(Long messageId, Long userId);

    /**
     * Lấy tất cả tin nhắn theo conversationId
     */
    List<Message> getMessagesByConversationId(Long conversationId);
}