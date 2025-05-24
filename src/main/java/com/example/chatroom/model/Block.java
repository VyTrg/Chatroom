package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name="UK_Block", columnNames = {"blocker_id", "blocked_id"})}) 
@AllArgsConstructor
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "blocked_id")
    private User blocked;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public Block() {
        this.createdAt = LocalDateTime.now();
    }
}
