package com.example.chatroom.controller;

import com.example.chatroom.model.Conversation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.chatroom.service.ConservationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConservationController {
    private final ConservationServiceImpl conversationService;

    @GetMapping("/user/{userId}")
    public List<Conversation> getAllConversationsForUser(@PathVariable Long userId) {
        return conversationService.getAllConversationsForUser(userId);
    }
}
