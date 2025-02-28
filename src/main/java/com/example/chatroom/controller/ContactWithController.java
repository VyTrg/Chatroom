package com.example.chatroom.controller;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.service.ContactWithServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactWithController {
    @Autowired
    private ContactWithServiceImpl contactWithService;

    @GetMapping
    public List<ContactWith> getContacts() {
        return contactWithService.getAllContactsWith();
    }

    @GetMapping("/{id}")
    public ContactWith getContactByUserId(@PathVariable("id") Long id) {
        ContactWith contactWith = contactWithService.getContactWithByUserId(id);
        return ResponseEntity.ok().body(contactWith).getBody();
    }

    @PostMapping
    public ContactWith createContact(@RequestBody ContactWith contactWith) {
        return contactWithService.saveContactWith(contactWith);
    }

    @PutMapping("/{id}")
    public ContactWith updateContact(@PathVariable("id") Long id, @RequestBody ContactWith contactWith) {
        ContactWith contact = contactWithService.getContactWithById(id);

//        contact.setContactOne(contactWith.getContactOne());
//        contact.setContactTwo(contactWith.getContactTwo());
//        contact.setCreatedAt(contactWith.getCreatedAt());
//        contact.setDeletedAt(contactWith.getDeletedAt());
        final ContactWith updatedContact = contactWithService.updateContactWith(contact);
        return ResponseEntity.ok().body(updatedContact).getBody();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable("id") Long id) {
        ContactWith contact = contactWithService.getContactWithById(id);
//        contact.setDeletedAt();
        contactWithService.deleteContactWith(contact);
        return ResponseEntity.ok().build();
    }
}
