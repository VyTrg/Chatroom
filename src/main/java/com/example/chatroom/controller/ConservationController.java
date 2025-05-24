package com.example.chatroom.controller;

import com.example.chatroom.chat.MessageType;
import com.example.chatroom.chat.NotificationMessage;
import com.example.chatroom.model.Conversation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.chatroom.service.ConservationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConservationController {
    private final ConservationServiceImpl conversationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/user/{userId}")
    public List<Conversation> getAllConversationsForUser(@PathVariable Long userId) {
        return conversationService.getAllConversationsForUser(userId);
    }
    @PostMapping("/group")
    public ResponseEntity<Conversation> createGroupConversation(
            @RequestBody List<Long> userIds,
            @RequestParam(required = false) String conversationName) {
        try {
            Conversation conversation = conversationService.createGroupConversation(userIds, conversationName);
            for (Long userId : userIds) {
                NotificationMessage notification = new NotificationMessage();
                notification.setType(MessageType.valueOf("GROUP_REQUEST"));  // message type for group notifications
                notification.setMessage("You have been added to a new group: " + (conversationName != null ? conversationName : "Unnamed Group"));
                notification.setUserId(userId);  // the user who receives this notification

                messagingTemplate.convertAndSend(
                        "/topic/notifications/" + userId,  // topic specific to this user
                        notification
                );
            }
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}