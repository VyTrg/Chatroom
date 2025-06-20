package com.example.chatroom.repository;

import com.example.chatroom.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query(value = "SELECT * FROM attachment WHERE message_id = ?1", nativeQuery = true)
    Attachment findByMessageId(Long messageId);

}
