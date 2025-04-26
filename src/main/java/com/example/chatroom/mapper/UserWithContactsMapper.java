package com.example.chatroom.mapper;

import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserWithContactsMapper {
    public UserWithContactsDTO toUserWithContactsDTO(User user, List<ContactWith> contacts) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        UserWithContactsDTO dto = new UserWithContactsDTO();
        dto.setUserId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setEnabled(user.getEnabled());
        dto.setProfilePicture(user.getProfilePicture());

        List<UserWithContactsDTO.ContactDTO> contactDTOs = contacts.stream()
                .map(contactWith -> {
                    User friend;
                    if (contactWith.getContactOne().getId().equals(user.getId())) {
                        friend = contactWith.getContactTwo();
                    } else {
                        friend = contactWith.getContactOne();
                    }

                    UserWithContactsDTO.ContactDTO contactDTO = new UserWithContactsDTO.ContactDTO();
                    contactDTO.setContactId(friend.getId());
                    contactDTO.setContactFirstName(friend.getFirstName());
                    contactDTO.setContactLastName(friend.getLastName());
                    contactDTO.setContactEmail(friend.getEmail());
                    contactDTO.setContactUsername(friend.getUsername());
                    contactDTO.setContactStatus(friend.getStatus());
                    contactDTO.setContactEnabled(friend.getEnabled());
                    contactDTO.setContactProfilePicture(friend.getProfilePicture());
                    return contactDTO;
                })
                .collect(Collectors.toList());

        dto.setContacts(contactDTOs);

        return dto;
    }
}
