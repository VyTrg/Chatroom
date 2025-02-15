package com.example.chatroom.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendVerificationEmail(String to, String confirmationUrl);
    void sendEmail(String to, String subject, String body);
}