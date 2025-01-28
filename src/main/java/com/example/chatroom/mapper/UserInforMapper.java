package com.example.chatroom.mapper;

import com.example.chatroom.dto.UserInforDTO;
import com.example.chatroom.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public class UserInforMapper {
    public UserInforDTO toUserInforDTO(User user) {
        UserInforDTO userInforDTO = new UserInforDTO();
        userInforDTO.setId(user.getId());
        userInforDTO.setFirstName(user.getFirstName());
        userInforDTO.setLastName(user.getLastName());
        userInforDTO.setProfilePicture(user.getProfilePicture());
        userInforDTO.setStatus(user.getStatus());
        return userInforDTO;
    }
    public User toUser(UserInforDTO userInforDTO) {
        return null;
    }
}
