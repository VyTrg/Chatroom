package com.example.chatroom.dto;

import com.example.chatroom.model.ContactWith;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Boolean status;
    private String profilePicture;
}
