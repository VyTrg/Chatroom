package com.example.chatroom.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConversationWithRoleDTO {
    // Getters and Setters
    private Long id;
    private String name;
    private Boolean isGroup;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt; // Conversation deleted_at field
    private LocalDateTime contactDeletedAt; // Contact_with deleted_at field
    private String role;
    private Long otherUserId;
    private Long blockerId;
    private Long userId;

    // Constructor
    public ConversationWithRoleDTO(Long id, String name, Boolean isGroup, String imageUrl,
                                   LocalDateTime createdAt, LocalDateTime deletedAt, LocalDateTime contactDeletedAt, 
                                   String role, Long otherUserId, Long blockerId, Long userId) {
        this.id = id;
        this.name = name;
        this.isGroup = isGroup;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.contactDeletedAt = contactDeletedAt;
        this.role = role!= null ? role : "MEMBER";
        this.otherUserId = otherUserId;
        this.blockerId = blockerId;
        this.userId = userId;
    }
    public ConversationWithRoleDTO() {
    }
}
