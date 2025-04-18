package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// @Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @Table(uniqueConstraints = {@UniqueConstraint(name="UK_UserMessageStatus", columnNames = {"user_id", "message_id"})})
public class UserMessageStatus {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne
    // @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // @ManyToOne
    // @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // @Column(nullable = false)
    private boolean read = false;

    // @Column(name = "delivered")
    private boolean delivered = false;

    // @Column(name = "read_at")
    private LocalDateTime readAt;

    // @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}