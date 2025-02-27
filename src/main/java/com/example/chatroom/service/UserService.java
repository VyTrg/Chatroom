package com.example.chatroom.service;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    UserWithContactsDTO getUserWithContactsDTOById(Long id);
}
