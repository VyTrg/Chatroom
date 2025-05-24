package com.example.chatroom.service;

import com.example.chatroom.model.ContactRequest;

import java.util.List;

public interface ContactRequestService {

    List<ContactRequest> getAllNotificationsForUser(Long userId);
    void acceptRequest(Long requestId);
    void declineRequest(Long requestId);
    void createRequest(Long userOne, Long userTwo);
}
