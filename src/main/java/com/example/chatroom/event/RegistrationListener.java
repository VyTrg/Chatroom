package com.example.chatroom.event;

import com.example.chatroom.event.OnRegistrationCompleteEvent;
import com.example.chatroom.model.User;
import com.example.chatroom.service.EmailService;
import com.example.chatroom.service.UserServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserServiceRegister userService;

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), confirmationUrl);
    }
}
