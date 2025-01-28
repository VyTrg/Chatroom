package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactWithDTO {
    private Long id;
    private UserInforDTO contactTwo;// who contacts with contact_one, meaning friend lists of user one
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
