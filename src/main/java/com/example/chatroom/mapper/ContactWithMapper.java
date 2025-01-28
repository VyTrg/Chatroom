package com.example.chatroom.mapper;

import com.example.chatroom.dto.ContactWithDTO;
import com.example.chatroom.dto.UserInforDTO;
import com.example.chatroom.model.ContactWith;

import java.util.HashSet;
import java.util.Set;

public class ContactWithMapper {
    private final UserInforMapper userInforMapper = new UserInforMapper();
    public ContactWithDTO toContactWithDTO(ContactWith contactWith) {
            ContactWithDTO contactWithDTO = new ContactWithDTO();
            contactWithDTO.setId(contactWith.getId());
            contactWithDTO.setContactTwo(userInforMapper.toUserInforDTO(contactWith.getContactTwo()));
            contactWithDTO.setCreatedAt(contactWith.getCreatedAt());
            contactWithDTO.setDeletedAt(contactWith.getDeletedAt());
        return contactWithDTO;
    }
}
