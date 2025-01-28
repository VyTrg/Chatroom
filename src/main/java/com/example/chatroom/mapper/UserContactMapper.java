package com.example.chatroom.mapper;


import com.example.chatroom.dto.ContactWithDTO;
import com.example.chatroom.dto.UserContactsDTO;
import com.example.chatroom.dto.UserInforDTO;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserContactMapper {
    private final UserInforMapper userInforMapper = new UserInforMapper();
    private final ContactWithMapper contactWithMapper = new ContactWithMapper();

    public UserContactsDTO toUserContactDTO(User user, List<ContactWith> contactWiths) {
        UserContactsDTO userContactDTO = new UserContactsDTO();
        UserInforDTO userInforDTO = new UserInforDTO();
        userInforDTO = userInforMapper.toUserInforDTO(user);
        userContactDTO.setUser(userInforDTO);
        List<ContactWithDTO> listContacts = new ArrayList<ContactWithDTO>();
        for (ContactWith contactWith : contactWiths) {
            ContactWithDTO contactWithDTO = new ContactWithDTO();
            contactWithDTO = contactWithMapper.toContactWithDTO(contactWith);
            listContacts.add(contactWithDTO);
        }
        userContactDTO.setContacts(listContacts);
        return userContactDTO;
    }
}
