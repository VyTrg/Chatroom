package com.example.chatroom.repository;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactWithRepository extends JpaRepository<ContactWith, Long> {

    ContactWith findContactWithsByContactOne_Id(Long contactOneId);

    // Tìm mối quan hệ bạn bè giữa 2 user cụ thể (theo cả 2 chiều)
    ContactWith findContactWithsByContactOne_IdAndContactTwo_Id(Long contactOneId, Long contactTwoId);

    ContactWith findContactWithsById(Long id);
    
    // Tìm liên hệ giữa hai người dùng (theo cả hai chiều)
//    @Query("SELECT cw FROM ContactWith cw " +
//            "WHERE (cw.contactOne.id = :userId1 AND cw.contactTwo.id = :userId2) " +
//            "OR (cw.contactOne.id = :userId2 AND cw.contactTwo.id = :userId1)")
//    Optional<ContactWith> findContactBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    // Trong ContactWithRepository.java
    @Query("SELECT cw FROM ContactWith cw " +
            "WHERE (cw.contactOne.id = :userId1 AND cw.contactTwo.id = :userId2) " +
            "OR (cw.contactOne.id = :userId2 AND cw.contactTwo.id = :userId1)")
    List<ContactWith> findContactBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
