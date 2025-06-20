package com.example.chatroom.controller;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.User;

import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public UserWithContactsDTO getUserInforById(@PathVariable(value = "id")Long userId) {
        return userService.getUserWithContactsDTOById(userId);
    }

//    @GetMapping("/username/{username}")
//    public Optional<User> getUserByUsername(@PathVariable(value = "username")String username) {
//        return userService.getUserByUsername(username);
//    }

    @GetMapping("/username/{username}")
    public UserWithContactsDTO getUserByUsername(@PathVariable(value = "username")String username) {
        return userService.getUserWithContactsDTOByUsername(username);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        if(StringUtils.hasText(userDetails.getHashPassword())){
            user.setHashPassword(passwordEncoder.encode(userDetails.getHashPassword()));
        }

        // user.setProfilePicture(userDetails.getProfilePicture());

        final User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id")Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userService.deleteUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/conversations/{userId}")
    public List<Conversation> getConversationsOfUser(@PathVariable Long userId) {
        return userService.getConversationsOfUser(userId);
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<User> uploadProfilePicture(@PathVariable Long id,
                                                     @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = userService.uploadProfilePicture(id, file);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search-contacts")
    public ResponseEntity<User> searchUser(@RequestParam("info") String search, @RequestParam("user") Long userId) {
        User user = userService.getUserByEmailOrUsernameInDiscussion(search, userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search-new")
    public ResponseEntity<User> searchNewUser(@RequestParam("info") String search, @RequestParam("user") Long userId) {
        User user = userService.findNewContact(search, userId);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/{userId}/block/{blockedUserId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.blockUser(userId, blockedUserId);
        
        // Gửi thông báo WebSocket đến cả hai người dùng
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "BLOCKED");
        payload.put("userId", blockedUserId);  
        payload.put("initiatorId", userId);
        payload.put("timestamp", LocalDateTime.now().toString());

        // Thông báo cho người dùng bị chặn
        messagingTemplate.convertAndSendToUser(
                blockedUserId.toString(),
                "/queue/contact-changes",
                payload
        );

        // Thông báo cho người chặn
        payload.put("type", "CONFIRMATION");
        payload.put("message", "Bạn đã chặn liên hệ thành công");
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/contact-changes",
                payload
        );
        
        return ResponseEntity.ok().build();
    }
}
