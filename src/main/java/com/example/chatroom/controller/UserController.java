package com.example.chatroom.controller;



import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.User;


import com.example.chatroom.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;



    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public UserWithContactsDTO getUserInforById(@PathVariable(value = "id")Long userId) {
        return userService.getUserWithContactsDTOById(userId);
    }

    @GetMapping("/username/{username}")
    public Optional<User> getUserByUsername(@PathVariable(value = "username")String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id")Long userId, @RequestBody User userDetails) {
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
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id")Long userId) {
//        User user = userService.getUserById(userId);
//        userService.deleteUser(user);
//        return ResponseEntity.ok().build();
        return null;
    }

}
