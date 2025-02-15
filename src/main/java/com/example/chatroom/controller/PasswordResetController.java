package com.example.chatroom.controller;

import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(UserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email không được để trống.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại trong hệ thống.");
        }

        // Tao JWT token có thoi gian het han
        String token = JwtUtil.generateToken(email, 15 * 60 * 1000);
        String resetLink = "http://localhost:8080/reset-password.html#token=" + token;

        // Gui email dat lai mat khau
        sendEmail(email, resetLink);

        return ResponseEntity.ok("Email đặt lại mật khẩu đã được gửi.");
    }

    private void sendEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu");
        message.setText("Nhấp vào liên kết để đặt lại mật khẩu: " + link);
        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password.html"; // Tra ve file tu thu muc /static/
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token và mật khẩu mới không được để trống.");
        }

        // Kiem tra token co hop le khong
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ hoặc đã hết hạn.");
        }

        // Lay email tu JWT Token
        String email = JwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }

        // Cap nhat mat khau moi
        User user = userOptional.get();
        user.setHashPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
    }
}
