package com.example.chatroom.service;

import com.example.chatroom.model.*;
import com.example.chatroom.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class ContactRequestServiceImpl implements ContactRequestService {

    @Autowired
    private ContactRequestRepository contactRequestRepository;

    @Autowired
    private ContactWithServiceImpl contactWithService;

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ContactRequest> getAllNotificationsForUser(Long userId) {
        return contactRequestRepository.findAllNotificationsForUser(userId);
    }

    @Override
    @Transactional
    public void acceptRequest(Long requestId) {
        ContactRequest request = contactRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (Boolean.TRUE.equals(request.getIsAccepted())) {
            throw new RuntimeException("Request already accepted");
        }

        // Re-fetch sender and receiver to ensure they are attached
        User sender = userRepository.findById(request.getSender().getId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        request.setIsAccepted(true);
        contactRequestRepository.save(request);

        // Create contact_with
        ContactWith contactWith = new ContactWith();
        contactWith.setContactOne(sender);
        contactWith.setContactTwo(receiver);
//        contactWith.setCreatedAt(LocalDateTime.now());
        contactWithService.saveContactWith(contactWith);

        // Create conversation
        Conversation conversation = new Conversation();
        conversation.setIsGroup(false);
        conversation.setName(sender.getFirstName() + ' ' + sender.getLastName());
        conversationRepository.save(conversation);

        // Add users to conversation
        addMemberToConversation(conversation, sender, "member");
        addMemberToConversation(conversation, receiver, "member");
    }


    @Override
    public void declineRequest(Long requestId) {
        ContactRequest request = contactRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setIsAccepted(false);
        contactRequestRepository.save(request);
    }

    @Override
    public void createRequest(Long userOne, Long userTwo) {
        User contactOne = userRepository.findById(userOne)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userOne + " not found"));
        User contactTwo = userRepository.findById(userTwo)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userTwo + " not found"));
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setReceiver(contactTwo);
        contactRequest.setSender(contactOne);
        contactRequest.setIsAccepted(null);
        contactRequest.setDeleteAt(null);
        contactRequestRepository.save(contactRequest);
    }


    private void addMemberToConversation(Conversation conversation, User user, String role) {
        ConversationMember member = new ConversationMember();
        member.setConversation(conversation);
        member.setUser(user);
        member.setJoinedAt(LocalDateTime.now());
        member.setRole(role);
        conversationMemberRepository.save(member);
    }

}
