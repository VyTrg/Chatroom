package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInforDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private Boolean status;
}
