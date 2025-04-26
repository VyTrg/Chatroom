package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter

public class UserWithContactsDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String email;
    private String username;
    private Boolean status;
    private Boolean enabled;
    private List<ContactDTO> contacts;

    @AllArgsConstructor
    @NoArgsConstructor
//    @Getter
//    @Setter
    public static class ContactDTO {
        private Long contactId;
        private String contactFirstName;
        private String contactLastName;
        private String contactProfilePicture;
        private String contactEmail;
        private String contactUsername;
        private Boolean contactStatus;
        private Boolean contactEnabled;
        private LocalDateTime createdAt;
        private LocalDateTime deletedAt;

        public Long getContactId() {
            return contactId;
        }

        public void setContactId(Long contactId) {
            this.contactId = contactId;
        }

        public String getContactFirstName() {
            return contactFirstName;
        }

        public void setContactFirstName(String contactFirstName) {
            this.contactFirstName = contactFirstName;
        }

        public String getContactLastName() {
            return contactLastName;
        }

        public void setContactLastName(String contactLastName) {
            this.contactLastName = contactLastName;
        }

        public String getContactProfilePicture() {
            return contactProfilePicture;
        }

        public void setContactProfilePicture(String contactProfilePicture) {
            this.contactProfilePicture = contactProfilePicture;
        }

        public Boolean getContactStatus() {
            return contactStatus;
        }

        public void setContactStatus(Boolean contactStatus) {
            this.contactStatus = contactStatus;
        }

        public Boolean getContactEnabled() {
            return contactEnabled;
        }

        public void setContactEnabled(Boolean contactEnabled) {
            this.contactEnabled = contactEnabled;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
        }

        public String getContactUsername() {
            return contactUsername;
        }

        public void setContactUsername(String contactUsername) {
            this.contactUsername = contactUsername;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
