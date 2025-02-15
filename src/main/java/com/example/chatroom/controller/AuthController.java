package com.example.chatroom.controller;

import com.example.chatroom.dto.UserDto;
import com.example.chatroom.service.UserServiceRegister;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserServiceRegister userService;

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
//        String response = userService.registerUser(userDto);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String result = userService.registerUser(userDto);
            response.put("message", result);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
