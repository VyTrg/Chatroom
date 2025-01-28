package com.example.chatroom.mapper;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserWithContactsMapper {
    public UserWithContactsDTO toUserWithContactsDTO(User user, List<ContactWith> contacts) {
        UserWithContactsDTO userWithContactsDTO = new UserWithContactsDTO();
        userWithContactsDTO.setUserId(user.getId());
        userWithContactsDTO.setFirstName(user.getFirstName());
        userWithContactsDTO.setLastName(user.getLastName());
        userWithContactsDTO.setStatus(user.getStatus());
        userWithContactsDTO.setEnabled(user.getEnabled());
        userWithContactsDTO.setProfilePicture(user.getProfilePicture());
        List<UserWithContactsDTO.ContactDTO> contactDTOs = new ArrayList<>();
        for (ContactWith contactWith : contacts) {
            UserWithContactsDTO.ContactDTO contactDTO = new UserWithContactsDTO.ContactDTO();
            contactDTO.setContactId(contactWith.getId());
            contactDTO.setContactFirstName(contactWith.getContactTwo().getFirstName());
            contactDTO.setContactLastName(contactWith.getContactTwo().getLastName());
            contactDTO.setContactStatus(contactWith.getContactTwo().getStatus());
            contactDTO.setContactProfilePicture(contactWith.getContactTwo().getProfilePicture());
            contactDTO.setContactEnabled(contactWith.getContactTwo().getEnabled());
            contactDTOs.add(contactDTO);
        }
        userWithContactsDTO.setContacts(contactDTOs);
        return userWithContactsDTO;
    }
}
