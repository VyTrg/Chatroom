package com.example.chatroom.controller;

import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.security.JwtUtil;
import com.example.chatroom.service.JwtBlacklistService;
import com.example.chatroom.dto.UserDto;
import com.example.chatroom.service.UserServiceRegister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final UserServiceRegister userService;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtBlacklistService jwtBlacklistService,
                          UserServiceRegister userService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtBlacklistService = jwtBlacklistService;
        this.userService = userService;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc không được cung cấp.");
        }

        token = token.substring(7); // Cắt bỏ "Bearer " ở đầu
        System.out.println(" Đang logout với token: " + token);

        jwtBlacklistService.addToBlacklist(token);

        return ResponseEntity.ok("Bạn đã đăng xuất thành công!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            System.out.println("User không tồn tại: " + username);
            return ResponseEntity.status(401).body("Tài khoản không tồn tại.");
        }

        User foundUser = user.get();

        if (!foundUser.getEnabled()) {
            System.out.println("User chưa kích hoạt: " + username);
            return ResponseEntity.status(403).body("Tài khoản chưa kích hoạt. Vui lòng xác minh email.");
        }

        System.out.println("Mật khẩu nhập vào: " + password);
        System.out.println("Mật khẩu trong DB: " + foundUser.getHashPassword());

        if (!passwordEncoder.matches(password, foundUser.getHashPassword())) {
            return ResponseEntity.status(401).body("Mật khẩu không đúng.");
        }

        String token = jwtUtil.generateToken(username);
        System.out.println("Token được tạo: " + token);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.registerNewUserAccount(userDto);
            String token = jwtUtil.generateToken(user.getUsername());

            response.put("message", "Đăng ký thành công! Vui lòng xác nhận email.");
            response.put("token", token);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
