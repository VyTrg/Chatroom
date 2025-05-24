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
@Table(uniqueConstraints = {@UniqueConstraint(name="UK_ContactRequest", columnNames = {"sender", "receiver"})})
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "delete_at")
    private LocalDateTime deleteAt;

    @Column(nullable = true, name = "is_accepted")
    private Boolean isAccepted;

    public ContactRequest() {
        this.createdAt = LocalDateTime.now();
    }
}
