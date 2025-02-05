package com.example.chatroom.controller;

import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email khong duoc de trong.");
        }

        System.out.println("Received request: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            System.out.println("Email not found: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email khong ton tai.");
        }

        String token = JwtUtil.generateToken(email);
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

        sendEmail(email, resetLink);

        return ResponseEntity.ok("Email dat lai mat khau da duoc gui.");
    }

    private void sendEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Dat lai mat khau");
        message.setText("Nhan vao lien ket de dat lai mat khau: " + link);
        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        if (!JwtUtil.isTokenValid(token)) {
            return "error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token va mat khau moi khong duoc de trong.");
        }

        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token khong hop le hoac da het han.");
        }

        String email = JwtUtil.extractEmail(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nguoi dung khong ton tai.");
        }

        User user = userOptional.get();
        user.setHashPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Mat khau da duoc cap nhat thanh cong.");
    }
}
