package com.example.chatroom.controller;

import com.example.chatroom.chat.MessageType;
import com.example.chatroom.chat.NotificationMessage;
import com.example.chatroom.model.ContactRequest;
import com.example.chatroom.repository.ContactRequestRepository;
import com.example.chatroom.service.ContactRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
public class ContactRequestController {
    @Autowired
    private ContactRequestService contactRequestService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ContactRequestRepository contactRequestRepository;

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable Long userId) {
        List<ContactRequest> notifications = contactRequestService.getAllNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Map<String, String>> acceptRequest(@PathVariable Long id) {
        contactRequestService.acceptRequest(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Request accepted.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<Map<String, String>> declineRequest(@PathVariable Long id) {
        contactRequestService.declineRequest(id);
//        messagingTemplate.convertAndSend("/topic/notification", "Request accepted.");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Request accepted.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRequest(@RequestParam("userOne") Long userOne, @RequestParam("userTwo") Long userTwo) {
        contactRequestService.createRequest(userOne, userTwo);
        MessageType messageType;
        NotificationMessage message = new NotificationMessage();
        message.setType(MessageType.valueOf("FRIEND_REQUEST")); // Set the type of message, e.g., FRIEND_REQUEST
        message.setMessage("You have a new friend request!"); // Friendly user message
        message.setUserId(userOne);
        // Send to recipient (userTwo)
        messagingTemplate.convertAndSendToUser(
                userTwo.toString(),              // WebSocket username (session id, Principal, or raw ID)
                "/queue/private",                // STOMP destination
                message                          // Message body
        );
        Map<String, String> response = new HashMap<>();
        response.put("message", "Request accepted.");
        return ResponseEntity.ok(response);
    }
}
