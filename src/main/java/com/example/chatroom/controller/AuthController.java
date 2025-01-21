package com.example.chatroom.controller;

import com.example.chatroom.model.User;
import com.example.chatroom.service.UserServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserServiceRegister userService;

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }
}
