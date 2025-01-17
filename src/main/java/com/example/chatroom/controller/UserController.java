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
}
