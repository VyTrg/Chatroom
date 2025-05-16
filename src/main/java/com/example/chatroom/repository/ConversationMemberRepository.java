package com.example.chatroom.repository;

import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {
    // Tìm tất cả thành viên trong một cuộc hội thoại
    @Query("SELECT cm.user FROM ConversationMember cm WHERE cm.conversation.id = :conversationId")
    List<User> findAllMembersInConversation(@Param("conversationId") Long conversationId);

    // Kiểm tra xem một người dùng có phải là thành viên của cuộc hội thoại không
    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);

    // Tìm bản ghi thành viên cụ thể
    Optional<ConversationMember> findByUserIdAndConversationId(Long userId, Long conversationId);

    // Đếm số thành viên trong một cuộc hội thoại
    long countByConversationId(Long conversationId);

    // Xóa một người dùng khỏi cuộc hội thoại
    void deleteByUserIdAndConversationId(Long userId, Long conversationId);

    // Tìm tất cả cuộc hội thoại mà một người dùng tham gia
    @Query("SELECT cm.conversation.id FROM ConversationMember cm WHERE cm.user.id = :userId")
    List<Long> findAllConversationIdsByUserId(@Param("userId") Long userId);

    // Kiểm tra xem hai người dùng có chung cuộc hội thoại nào không
    @Query("SELECT COUNT(cm1) > 0 FROM ConversationMember cm1, ConversationMember cm2 " +
            "WHERE cm1.conversation = cm2.conversation " +
            "AND cm1.user.id = :userId1 AND cm2.user.id = :userId2")
    boolean haveSharedConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Lấy thành viên nhóm
    List<ConversationMember> findAllByConversationId(Long conversationId);

    boolean existsByConversationAndUser(Conversation group, User user);
}