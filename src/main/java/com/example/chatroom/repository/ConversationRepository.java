package com.example.chatroom.repository;

import com.example.chatroom.dto.ConversationWithRoleDTO;
import com.example.chatroom.model.ContactWith;
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
    @Query(value = "SELECT TOP 1 c.* FROM dbo.conversation c " +
            "WHERE c.is_group = 0 " +
            "AND c.deleted_at IS NULL " +
            "AND EXISTS (SELECT 1 FROM dbo.conversation_member cm1 WHERE cm1.conversation_id = c.id AND cm1.user_id = :userId1) " +
            "AND EXISTS (SELECT 1 FROM dbo.conversation_member cm2 WHERE cm2.conversation_id = c.id AND cm2.user_id = :userId2) " +
            "AND (SELECT COUNT(*) FROM dbo.conversation_member cm WHERE cm.conversation_id = c.id) = 2 " +
            "ORDER BY c.created_at DESC",
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

    // Thay thế JPQL query phức tạp bằng native query rõ ràng và dễ bảo trì hơn
    @Query(value = "SELECT c.id AS conversation_id, c.name, c.is_group, c.image_url, c.created_at, c.deleted_at, " +
            "cw.deleted_at AS contact_deleted_at, cm.role, " +
            "CASE WHEN c.is_group = 0 THEN other_user.id ELSE NULL END AS other_user_id, " +
            "b.blocker_id AS blocker_id, :userId AS user_id " +
            "FROM conversation c " +
            "JOIN conversation_member cm ON cm.conversation_id = c.id AND cm.user_id = :userId " +
            "LEFT JOIN ( " +
            "    SELECT cm2.conversation_id, cm2.user_id " +
            "    FROM conversation_member cm2 " +
            "    WHERE cm2.user_id != :userId " +
            ") AS other_user ON c.id = other_user.conversation_id AND c.is_group = 0 " +
            "LEFT JOIN contact_with cw ON ((cw.contact_one_id = :userId AND cw.contact_two_id = other_user.user_id) " +
            "    OR (cw.contact_one_id = other_user.user_id AND cw.contact_two_id = :userId)) " +
            "    AND c.is_group = 0 " +
            "LEFT JOIN block b ON ((b.blocker_id = :userId AND b.blocked_id = other_user.user_id) " +
            "    OR (b.blocked_id = :userId AND b.blocker_id = other_user.user_id)) " +
            "    AND c.is_group = 0 " +
            "WHERE c.deleted_at IS NULL " +
            "ORDER BY c.created_at DESC",
            nativeQuery = true)
    List<ConversationWithRoleDTO> findAllConversationsForUserWithRole(@Param("userId") Long userId);

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

    // Tìm cuộc hội thoại dựa trên contactId
    @Query(value = "SELECT c.* FROM conversation c " +
            "JOIN conversation_member cm1 ON c.id = cm1.conversation_id " +
            "JOIN conversation_member cm2 ON c.id = cm2.conversation_id " +
            "JOIN contact_with cw ON (cw.contact_one_id = cm1.user_id AND cw.contact_two_id = cm2.user_id) " +
            "   OR (cw.contact_one_id = cm2.user_id AND cw.contact_two_id = cm1.user_id) " +
            "WHERE cw.id = :contactId AND c.is_group = 0 AND c.deleted_at IS NULL AND cw.deleted_at IS NULL",
            nativeQuery = true)
    List<Conversation> findByContactId(@Param("contactId") Long contactId);

    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationMember cm ON cm.conversation.id = c.id " +
            "WHERE cm.user.id = :userId AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    List<Conversation> findSimpleConversationsForUser(@Param("userId") Long userId);

    @Query("SELECT cw FROM ContactWith cw " +
            "WHERE (cw.contactOne.id = :userId1 AND cw.contactTwo.id = :userId2) " +
            "OR (cw.contactOne.id = :userId2 AND cw.contactTwo.id = :userId1)")
    Optional<ContactWith> findContactBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT cm.conversation FROM ConversationMember cm WHERE cm.user.id = :userId")
    boolean existsByConversationAndUser(Long conversationoId, Long userId);

}