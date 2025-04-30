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
    // Tìm cuộc hội thoại riêng tư giữa hai người dùng (native query)
    @Query(value = "SELECT * FROM conversation c " +
            "WHERE c.is_group = 0 " +
            "AND EXISTS (SELECT 1 FROM conversation_member cm1 WHERE cm1.conversation_id = c.id AND cm1.user_id = :userId1) " +
            "AND EXISTS (SELECT 1 FROM conversation_member cm2 WHERE cm2.conversation_id = c.id AND cm2.user_id = :userId2) " +
            "AND (SELECT COUNT(*) FROM conversation_member cm WHERE cm.conversation_id = c.id) = 2",
            nativeQuery = true)
    Optional<Conversation> findPrivateConversationBetween(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    // Tìm tất cả cuộc hội thoại mà một người dùng tham gia (native query)
    @Query(value = "SELECT c.* FROM conversation c JOIN conversation_member cm ON cm.conversation_id = c.id " +
            "WHERE cm.user_id = :userId ORDER BY c.created_at DESC",
            nativeQuery = true)
    List<Conversation> findAllConversationsForUser(@Param("userId") Long userId);

    // Tìm các cuộc hội thoại nhóm mà một người dùng tham gia (native query)
    @Query(value = "SELECT c.* FROM conversation c JOIN conversation_member cm ON cm.conversation_id = c.id " +
            "WHERE cm.user_id = :userId AND c.is_group = 1 ORDER BY c.created_at DESC",
            nativeQuery = true)
    List<Conversation> findGroupConversationsForUser(@Param("userId") Long userId);

    // Tìm các cuộc hội thoại riêng tư mà một người dùng tham gia (native query)
    @Query(value = "SELECT c.* FROM conversation c JOIN conversation_member cm ON cm.conversation_id = c.id " +
            "WHERE cm.user_id = :userId AND c.is_group = 0 ORDER BY c.created_at DESC",
            nativeQuery = true)
    List<Conversation> findPrivateConversationsForUser(@Param("userId") Long userId);

    // Tìm cuộc hội thoại theo tên (native query)
    @Query(value = "SELECT * FROM conversation WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Conversation> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
}