package com.example.chatroom.mapper;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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
            User friend;
            if(contactWith.getContactOne().getId().equals(user.getId())) {//check user's id
                friend = contactWith.getContactTwo();
            }
            else {
                friend = contactWith.getContactOne();
            }
            contactDTO.setContactId(friend.getId());
            contactDTO.setContactFirstName(friend.getFirstName());
            contactDTO.setContactLastName(friend.getLastName());
            contactDTO.setContactStatus(friend.getStatus());
            contactDTO.setContactProfilePicture(friend.getProfilePicture());
            contactDTO.setContactEnabled(friend.getEnabled());
            contactDTOs.add(contactDTO);
        }
        userWithContactsDTO.setContacts(contactDTOs);
        return userWithContactsDTO;
    }
}
