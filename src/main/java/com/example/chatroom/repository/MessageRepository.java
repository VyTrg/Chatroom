package com.example.chatroom.repository;

import com.example.chatroom.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Tìm tin nhắn theo cuộc hội thoại với phân trang
    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    // Tìm tin nhắn theo người gửi với phân trang
    Page<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

    // Tìm tin nhắn chưa đọc của một người dùng trong một cuộc hội thoại
    @Query("SELECT m FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId " +
            "AND m.isRead = false " +
            "ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessagesForUserInConversation(
            @Param("userId") Long userId,
            @Param("conversationId") Long conversationId);

    // Tìm tin nhắn theo nội dung (tìm kiếm đơn giản)
    List<Message> findByMessageTextContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);


    @Query("SELECT m FROM Message m " +
            "WHERE m.conversation.id IN " +
            "(SELECT cm.conversation.id FROM ConversationMember cm WHERE cm.user.id = :userId) " +
            "AND m.createdAt = " +
            "(SELECT MAX(m2.createdAt) FROM Message m2 WHERE m2.conversation.id = m.conversation.id) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findLatestMessagesForUser(@Param("userId") Long userId);
}