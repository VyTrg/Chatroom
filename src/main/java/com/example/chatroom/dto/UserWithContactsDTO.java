package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserWithContactsDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private Boolean status;
    private Boolean enabled;
    private List<ContactDTO> contacts;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ContactDTO {
        private Long contactId;
        private String contactFirstName;
        private String contactLastName;
        private String contactProfilePicture;
        private Boolean contactStatus;
        private Boolean contactEnabled;
        private LocalDateTime createdAt;
        private LocalDateTime deletedAt;
    }
}
