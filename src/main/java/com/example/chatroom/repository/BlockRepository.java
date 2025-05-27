package com.example.chatroom.repository;

import com.example.chatroom.model.Block;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM block WHERE blocker_id = ?1 AND blocked_id = ?2", nativeQuery = true)
    void deleteByBlockerAndBlocked(Long blocker, Long blocked);

}
