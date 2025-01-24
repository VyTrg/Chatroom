package com.example.chatroom.mapper;

import com.example.chatroom.dto.UserDTO;
import com.example.chatroom.model.User;

public class UserMapper {
    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setStatus(user.getStatus());
        userDTO.setProfilePicture(user.getProfilePicture());
        return userDTO;
    }
}
