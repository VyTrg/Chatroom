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
    @Query("SELECT c FROM Conversation c " +
            "WHERE c.isGroup = false " +
            "AND EXISTS (SELECT cm1 FROM ConversationMember cm1 WHERE cm1.conversation = c AND cm1.user.id = :userId1) " +
            "AND EXISTS (SELECT cm2 FROM ConversationMember cm2 WHERE cm2.conversation = c AND cm2.user.id = :userId2) " +
            "AND (SELECT COUNT(cm) FROM ConversationMember cm WHERE cm.conversation = c) = 2")
    Optional<Conversation> findPrivateConversationBetween(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    // Tìm tất cả cuộc hội thoại mà một người dùng tham gia
    @Query("SELECT c FROM Conversation c JOIN c.conversationMembers cm " +
            "WHERE cm.user.id = :userId ORDER BY c.createdAt DESC")
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