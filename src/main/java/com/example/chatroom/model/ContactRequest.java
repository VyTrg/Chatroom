package com.example.chatroom.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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


    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime deleteAt;

    @Column(nullable = false)
    private Boolean isAccepted;

    public ContactRequest() {
        this.createdAt = LocalDateTime.now();
    }
}
