package com.example.chatroom.repository;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD

=======
import org.springframework.stereotype.Repository;

@Repository
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
public interface ContactWithRepository extends JpaRepository<ContactWith, Long> {

    ContactWith findContactWithsByContactOne_Id(Long contactOneId);
}
