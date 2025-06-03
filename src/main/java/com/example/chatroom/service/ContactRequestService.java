package com.example.chatroom.service;

import com.example.chatroom.model.ContactRequest;

import java.util.List;

public interface ContactRequestService {

    List<ContactRequest> getAllNotificationsForUser(Long userId);
    ContactRequest acceptRequest(Long requestId);
    ContactRequest declineRequest(Long requestId);
    void createRequest(Long userOne, Long userTwo);
}
