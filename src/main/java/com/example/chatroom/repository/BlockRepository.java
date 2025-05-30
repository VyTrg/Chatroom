package com.example.chatroom.repository;

import com.example.chatroom.model.Block;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM block WHERE blocker_id = ?1 AND blocked_id = ?2", nativeQuery = true)
    void deleteByBlockerAndBlocked(Long blocker, Long blocked);
    
    // Tìm thông tin chặn giữa hai người dùng
    @Query("SELECT b FROM Block b WHERE " +
           "(b.blocker.id = :userId1 AND b.blocked.id = :userId2) OR " +
           "(b.blocker.id = :userId2 AND b.blocked.id = :userId1)")
    Optional<Block> findBlockBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
