package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name="UK_Block", columnNames = {"blocker", "blocked"})})
@AllArgsConstructor
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "blocker_id",insertable=false, updatable=false)
    private User blocker;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "blocked_id",insertable=false, updatable=false)
    private User blocked;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public Block() {
        this.createdAt = LocalDateTime.now();
    }
}
