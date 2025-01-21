package com.example.chatroom.service;

import com.example.chatroom.model.ContactWith;

import java.util.List;

public interface ContactWithService {
    List<ContactWith> getAllContactsWith();

    ContactWith getContactWithByUserId(Long id);

    ContactWith saveContactWith(ContactWith contactWith);

    ContactWith updateContactWith(ContactWith contactWith);

    void deleteContactWith(ContactWith contactWith);

    ContactWith getContactWithById(Long id);


}
