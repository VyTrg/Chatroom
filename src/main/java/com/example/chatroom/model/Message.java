package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id",insertable=false, updatable=false)
    private User sender;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "conversation_id",insertable=false, updatable=false)
    private Conversation conversation;

    @Column(nullable = false, name = "message_text")
    private String messageText;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(nullable = true, name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "is_read")
    private Boolean isRead;

    public Message() {
        this.createdAt = LocalDateTime.now();
    }
}
