package com.example.chatroom.controller;


import com.example.chatroom.exception.ApiRequestException;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id")Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User With id=" + userId + " Not Found"));
        return ResponseEntity.ok().body(user).getBody();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id")Long userId, @RequestBody User userDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User With id=" + userId + " Not Found"));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        user.setHashPassword(userDetails.getHashPassword());
        user.setProfilePicture(userDetails.getProfilePicture());

        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id")Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User With id=" + userId + " Not Found"));
        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

}
