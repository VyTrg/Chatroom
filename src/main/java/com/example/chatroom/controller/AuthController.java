package com.example.chatroom.controller;

<<<<<<< HEAD
import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.security.JwtUtil;
import com.example.chatroom.service.JwtBlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
=======
import com.example.chatroom.dto.UserDto;
import com.example.chatroom.service.UserServiceRegister;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
>>>>>>> 9c654eab582b3e21472de4610babb8be24e61c0c

@RestController
@RequestMapping("/api/auth")
public class AuthController {
<<<<<<< HEAD
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;

    // Constructor duy nhất
    public AuthController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtBlacklistService jwtBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        jwtBlacklistService.addToBlacklist(token);

        return ResponseEntity.ok("Bạn đã đăng xuất thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getHashPassword())) {
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
=======

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

>>>>>>> 9c654eab582b3e21472de4610babb8be24e61c0c
}
