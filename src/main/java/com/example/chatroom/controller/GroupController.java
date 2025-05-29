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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @PostMapping("/add-members")
    public ResponseEntity<?> addMembersToGroup(@RequestBody Map<String, Object> body) {
        Map<String, Object> error = new HashMap<>();

        try {
            Long groupId = Long.valueOf(body.get("groupId").toString());

            @SuppressWarnings("unchecked")
            List<Object> userIdsRaw = (List<Object>) body.get("userIds");

            if (userIdsRaw == null || userIdsRaw.isEmpty()) {
                error.put("error", "error");
                return ResponseEntity.badRequest().body(error);
            }

            List<Long> userIds = userIdsRaw.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();

            String role = "member";

            Conversation group = conversationRepository.findById(groupId).orElse(null);
            if (group == null || !Boolean.TRUE.equals(group.getIsGroup())) {
                error.put("error", "error");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> users = userRepository.findAllById(userIds);
            if (users.size() != userIds.size()) {
                error.put("error", "Invalid Users");
                return ResponseEntity.badRequest().body(error);
            }

            List<String> addedUsers = new ArrayList<>();
            List<String> alreadyMembers = new ArrayList<>();

            for (User user : users) {
                boolean exists = conversationMemberRepository.existsByUserIdAndConversationId(user.getId(), groupId);
                if (exists) {
                    error.put("error", "Users have been in group");
                    return ResponseEntity.badRequest().body(error);
                }

                ConversationMember cm = new ConversationMember();
                cm.setConversation(group);
                cm.setUser(user);
                cm.setRole(role);
                cm.setJoinedAt(LocalDateTime.now());

                conversationMemberRepository.save(cm);
                addedUsers.add(user.getUsername());
                String name = user.getFirstName() + " " + user.getLastName();
                group.setName(group.getName() + "," + name);

            }
            conversationRepository.save(group);
            Map<String, Object> response = new HashMap<>();
            response.put("groupId", groupId);
            response.put("addedUsers", addedUsers);
            response.put("alreadyMembers", alreadyMembers);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            error.put("error", "Error" + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }



}