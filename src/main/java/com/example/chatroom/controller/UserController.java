package com.example.chatroom.controller;


import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new ResolutionException("User not found"));
        return ResponseEntity.ok().body(user).getBody();

    }
}
