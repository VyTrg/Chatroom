package com.example.chatroom.controller;



<<<<<<< HEAD
import com.example.chatroom.model.User;

=======
import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.User;


>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
import com.example.chatroom.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

<<<<<<< HEAD
=======


>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
<<<<<<< HEAD
    public User getUserById(@PathVariable(value = "id")Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok().body(user).getBody();
=======
    public UserWithContactsDTO getUserInforById(@PathVariable(value = "id")Long userId) {
        return userService.getUserWithContactsDTOById(userId);
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id")Long userId, @RequestBody User userDetails) {
<<<<<<< HEAD
        User user = userService.getUserById(userId);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        user.setHashPassword(userDetails.getHashPassword());
        user.setProfilePicture(userDetails.getProfilePicture());

        final User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
=======
//        User user = userService.getUserById(userId);
//        user.setFirstName(userDetails.getFirstName());
//        user.setLastName(userDetails.getLastName());
//        user.setEmail(userDetails.getEmail());
//        user.setUsername(userDetails.getUsername());
//        user.setHashPassword(userDetails.getHashPassword());
//        user.setProfilePicture(userDetails.getProfilePicture());
//
//        final User updatedUser = userService.updateUser(user);
//        return ResponseEntity.ok(updatedUser);
        return null;
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id")Long userId) {
<<<<<<< HEAD
        User user = userService.getUserById(userId);
        userService.deleteUser(user);
        return ResponseEntity.ok().build();
=======
//        User user = userService.getUserById(userId);
//        userService.deleteUser(user);
//        return ResponseEntity.ok().build();
        return null;
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    }

}
