package com.example.chatroom.repository;

import com.example.chatroom.model.UserMessageStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Component
public class UserMessageStatusRepository {
    // Tìm trạng thái một tin nhắn đối với người dùng
    public Optional<UserMessageStatus> findByUserIdAndMessageId(Long userId, Long messageId) {
        return Optional.empty(); // Trả về rỗng khi được gọi
    }

    // Lấy danh sách người dùng đã đọc tin nhắn
    // @Query("SELECT ums.user.id FROM UserMessageStatus ums WHERE ums.message.id = :messageId AND ums.read = true")
    public List<Long> findUserIdsWhoReadMessage(/* @Param("messageId") */ Long messageId) {
        return Collections.emptyList(); // Trả về danh sách rỗng
    }

    // Lấy thời điểm đọc tin nhắn của người dùng
    // @Query("SELECT ums.readAt FROM UserMessageStatus ums WHERE ums.user.id = :userId AND ums.message.id = :messageId")
    public Optional<LocalDateTime> findReadTimeByUserAndMessage(/* @Param("userId") */ Long userId, /* @Param("messageId") */ Long messageId) {
        return Optional.empty(); // Trả về rỗng
    }

    // Đánh dấu đã đọc cho một loạt tin nhắn
    // @Modifying
    // @Query("UPDATE UserMessageStatus ums SET ums.read = true, ums.readAt = :now " +
    //        "WHERE ums.user.id = :userId AND ums.message.id IN :messageIds AND ums.read = false")
    public int markMessagesAsRead(/* @Param("userId") */ Long userId, /* @Param("messageIds") */ List<Long> messageIds, /* @Param("now") */ LocalDateTime now) {
        return 0; // Giả lập không tin nhắn nào được cập nhật
    }

    // Đếm số tin nhắn chưa đọc trong cuộc hội thoại
    // @Query("SELECT COUNT(ums) FROM UserMessageStatus ums " +
    //        "WHERE ums.user.id = :userId AND ums.message.conversation.id = :conversationId AND ums.read = false")
    public long countUnreadMessagesInConversation(/* @Param("userId") */ Long userId, /* @Param("conversationId") */ Long conversationId) {
        return 0; // Giả lập không có tin nhắn chưa đọc
    }
}