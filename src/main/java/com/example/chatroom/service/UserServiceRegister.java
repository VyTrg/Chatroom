package com.example.chatroom.service;

import com.example.chatroom.dto.UserDto;
import com.example.chatroom.exception.UserAlreadyExistException;
import com.example.chatroom.model.User;
import com.example.chatroom.model.VerificationToken;

public interface UserServiceRegister {
    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;
    String verifyUser(String token);
    String registerUser(UserDto userDto);
    void createVerificationToken(User user, String token);
    VerificationToken getVerificationToken(String token);
    void saveRegisteredUser(User user);
}
