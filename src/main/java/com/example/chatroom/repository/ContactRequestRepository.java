package com.example.chatroom.repository;

import com.example.chatroom.model.ContactRequest;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    @Query(value = "SELECT * FROM contact_request WHERE (sender_id =?1 OR receiver_id = ?1) AND delete_at IS NULL ORDER BY created_at DESC", nativeQuery = true)
    List<ContactRequest> findAllNotificationsForUser(@Param("userId") Long userId);
}
