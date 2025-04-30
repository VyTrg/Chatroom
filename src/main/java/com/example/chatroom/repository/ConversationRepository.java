package com.example.chatroom.repository;

import com.example.chatroom.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Tìm cuộc hội thoại riêng tư giữa hai người dùng
    @Query(value = "SELECT c.* FROM dbo.conversation c " +
            "WHERE c.is_group = 0 " +
            "AND EXISTS (SELECT 1 FROM dbo.conversation_member cm1 WHERE cm1.conversation_id = c.id AND cm1.user_id = :userId1) " +
            "AND EXISTS (SELECT 1 FROM dbo.conversation_member cm2 WHERE cm2.conversation_id = c.id AND cm2.user_id = :userId2) " +
            "AND (SELECT COUNT(*) FROM dbo.conversation_member cm WHERE cm.conversation_id = c.id) = 2",
            nativeQuery = true)
    Optional<Conversation> findPrivateConversationBetween(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    // Tìm tất cả cuộc hội thoại mà một người dùng tham gia
    @Query(value = "SELECT c.* FROM conversation c " +
            "INNER JOIN conversation_member cm ON c.id = cm.conversation_id " +
            "WHERE cm.user_id = :userId " +
            "ORDER BY c.created_at DESC",
            nativeQuery = true)
    List<Conversation> findAllConversationsForUser(@Param("userId") Long userId);

    // Tìm các cuộc hội thoại nhóm mà một người dùng tham gia
    @Query("SELECT c FROM Conversation c JOIN c.conversationMembers cm " +
            "WHERE cm.user.id = :userId AND c.isGroup = true ORDER BY c.createdAt DESC")
    List<Conversation> findGroupConversationsForUser(@Param("userId") Long userId);

    // Tìm các cuộc hội thoại riêng tư mà một người dùng tham gia
    @Query("SELECT c FROM Conversation c JOIN c.conversationMembers cm " +
            "WHERE cm.user.id = :userId AND c.isGroup = false ORDER BY c.createdAt DESC")
    List<Conversation> findPrivateConversationsForUser(@Param("userId") Long userId);

    // Tìm cuộc hội thoại theo tên (tìm kiếm)
    List<Conversation> findByNameContainingIgnoreCase(String keyword);
}