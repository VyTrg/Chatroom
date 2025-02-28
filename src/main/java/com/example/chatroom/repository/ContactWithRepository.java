package com.example.chatroom.repository;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContactWithRepository extends JpaRepository<ContactWith, Long> {

    ContactWith findContactWithsByContactOne_Id(Long contactOneId);
}
