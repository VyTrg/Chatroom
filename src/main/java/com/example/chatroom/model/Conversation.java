package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name="UK_Block", columnNames = {"blocker", "blocked"})})
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "is_group")
    private Boolean isGroup;

    @Column(nullable = true, name = "image_url")
    private String imageUrl;//image url here

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public Conversation() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConversationMember> conversationMembers;
}
