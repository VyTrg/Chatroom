package com.example.chatroom.service;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    UserWithContactsDTO getUserWithContactsDTOById(Long id);

    Optional<User> getUserByUsername(String username);

    // Lấy tất cả conversation mà user tham gia
    List<Conversation> getConversationsOfUser(Long userId);
    UserWithContactsDTO getUserWithContactsDTOByUsername(String username);

    Optional<User> getUserById(Long id);

    User uploadProfilePicture(Long userId, MultipartFile file) throws IOException;

    User getUserByEmailOrUsernameInDiscussion(String search, Long userId);

    User findNewContact(String search, Long userId);

}
