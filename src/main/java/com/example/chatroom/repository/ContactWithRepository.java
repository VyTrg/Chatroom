package com.example.chatroom.repository;

import com.example.chatroom.model.ContactWith;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;

@Repository

public interface ContactWithRepository extends JpaRepository<ContactWith, Long> {

    ContactWith findContactWithsByContactOne_Id(Long contactOneId);
}
