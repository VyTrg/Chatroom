package com.example.chatroom.repository;


import com.example.chatroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
=======

public interface UserRepository extends JpaRepository<User, Long> {

>>>>>>> 656a5f37f2d1e12b1886acd83faa1157c10a0f91
}
