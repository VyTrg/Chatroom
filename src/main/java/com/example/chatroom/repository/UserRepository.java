package com.example.chatroom.repository;


import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from contact_with where contact_one_id = ?1 and contact_two_id <> ?1 or contact_two_id = ?1 and contact_one_id <> ?1",nativeQuery = true)
    List<ContactWith> findAllContacts(Long userId);
}
