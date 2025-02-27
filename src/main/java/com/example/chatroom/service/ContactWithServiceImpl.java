package com.example.chatroom.service;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.repository.ContactWithRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ContactWithServiceImpl implements ContactWithService{
    @Autowired
    private ContactWithRepository contactWithRepository;

    @Override
    public List<ContactWith> getAllContactsWith() {
        return contactWithRepository.findAll();
    }

    @Override
    public ContactWith getContactWithByUserId(Long id) {
        return contactWithRepository.findContactWithsByContactOne_Id(id);
    }

    @Override
    public ContactWith saveContactWith(ContactWith contactWith) {
        return contactWithRepository.save(contactWith);
    }

    @Override
    public ContactWith updateContactWith(ContactWith contactWith) {
        return contactWithRepository.save(contactWith);
    }

    @Override
    public void deleteContactWith(ContactWith contactWith) {
        contactWithRepository.delete(contactWith);
    }

    @Override
    public ContactWith getContactWithById(Long id) {
        return contactWithRepository.findById(id).get();
    }
}
