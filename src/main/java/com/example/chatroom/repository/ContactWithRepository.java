package com.example.chatroom.repository;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContactWithRepository extends JpaRepository<ContactWith, Long> {

    ContactWith findContactWithsByContactOne_Id(Long contactOneId);

    // Tìm mối quan hệ bạn bè giữa 2 user cụ thể (theo cả 2 chiều)
    ContactWith findContactWithsByContactOne_IdAndContactTwo_Id(Long contactOneId, Long contactTwoId);
}
