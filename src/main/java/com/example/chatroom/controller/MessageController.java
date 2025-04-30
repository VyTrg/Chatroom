package com.example.chatroom.controller;

import com.example.chatroom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.chatroom.model.Message;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/latest/{userId}")
    public List<Message> getLatestMessagesForUser(@PathVariable Long userId) {
        return messageService.getLatestMessagesForUser(userId);
    }

    @GetMapping("/conversation/{conversationId}")
    public List<Message>getMessagesByConversation(@PathVariable Long conversationId) {
        return messageService.getMessagesByConversation(conversationId);
    }
}
