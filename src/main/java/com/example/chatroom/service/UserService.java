package com.example.chatroom.service;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    UserWithContactsDTO getUserWithContactsDTOById(Long id);

    Optional<User> getUserByUsername(String username);

    UserWithContactsDTO getUserWithContactsDTOByUsername(String username);

    Optional<User> getUserById(Long id);
}
