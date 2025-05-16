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

    @Query(value = "SELECT m.* FROM dbo.message m " +
            "INNER JOIN [user] u ON m.user_id = u.id " +
            "WHERE m.conversation_id = :conversationId " +
            "ORDER BY m.created_at ASC",
            nativeQuery = true)
    List<Message> findMessagesByConversationId(@Param("conversationId") Long conversationId);

    @Query(value = "SELECT m.* FROM dbo.message m " +
            "WHERE m.conversation_id IN (SELECT cm.conversation_id FROM dbo.conversation_member cm WHERE cm.user_id = :userId) " +
            "AND m.created_at = (SELECT MAX(m2.created_at) FROM dbo.message m2 WHERE m2.conversation_id = m.conversation_id) " +
            "ORDER BY m.created_at DESC",
            nativeQuery = true)
    List<Message> findLatestMessagesForUser(@Param("userId") Long userId);
}