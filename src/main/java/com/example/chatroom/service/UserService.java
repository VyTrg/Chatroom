package com.example.chatroom.service;

<<<<<<< HEAD
=======
import com.example.chatroom.dto.UserWithContactsDTO;
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
import com.example.chatroom.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

<<<<<<< HEAD
    User getUserById(Long id);
=======
    UserWithContactsDTO getUserWithContactsDTOById(Long id);
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
}
