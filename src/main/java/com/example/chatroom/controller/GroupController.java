package com.example.chatroom.controller;

import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @RequestParam List<Long> userIds
    ) {

        // Lấy danh sách user từ DB
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            return ResponseEntity.badRequest().body("Có userId không tồn tại!");
        }

        // Sinh tên group từ username các user
        String groupName = users.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(" & "));

        Conversation group = new Conversation();
        group.setName(groupName);
        group.setIsGroup(true);
        group.setCreatedAt(LocalDateTime.now());
        // Lưu group và lấy lại instance đã được quản lý
        group = conversationRepository.save(group);

        // Thêm từng user vào group, luôn dùng entity từ DB
        for (User user : users) {
            ConversationMember cm = new ConversationMember();
            cm.setConversation(group); // LUÔN dùng instance đã save ở trên!
            cm.setUser(user);
            cm.setRole("member");
            cm.setJoinedAt(LocalDateTime.now());
            conversationMemberRepository.save(cm);
        }

        // Thêm log
        logger.info("Tạo group với id = {} và các thành viên: {}", group.getId(), users.stream().map(User::getUsername).collect(Collectors.joining(", ")));

        return ResponseEntity.ok("Tạo group thành công với id: " + group.getId());

        /*
        Conversation group = new Conversation();
        group.setName(groupName);
        group.setIsGroup(true);
        group.setCreatedAt(LocalDateTime.now());
        conversationRepository.save(group);

        for (User user : users) {
            ConversationMember cm = new ConversationMember();
            cm.setConversation(group);
            cm.setUser(user);
            cm.setRole("member");
            cm.setJoinedAt(LocalDateTime.now());
            conversationMemberRepository.save(cm);
        }
        return ResponseEntity.ok("Tạo group thành công với id: " + group.getId());
        */
    }

    // API thêm thành viên vào group đã có, cho phép phân quyền
    @PostMapping("/add-member")
    public ResponseEntity<?> addMemberToGroup(
            @RequestParam Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "member") String role
    ) {
        Conversation group = conversationRepository.findById(groupId).orElse(null);
        if (group == null || !Boolean.TRUE.equals(group.getIsGroup())) {
            return ResponseEntity.badRequest().body("Group không tồn tại hoặc không phải group");
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại!");
        }
        // Kiểm tra nếu user đã là thành viên
        boolean exists = conversationMemberRepository.existsByConversationAndUser(group, user);
        if (exists) {
            return ResponseEntity.badRequest().body("User đã là thành viên của group");
        }
        ConversationMember cm = new ConversationMember();
        cm.setConversation(group);
        cm.setUser(user);
        cm.setRole(role); // "member" hoặc "admin"
        cm.setJoinedAt(LocalDateTime.now());
        conversationMemberRepository.save(cm);
        return ResponseEntity.ok("Đã thêm user " + userId + " vào group " + groupId + " với quyền: " + role);
    }
}