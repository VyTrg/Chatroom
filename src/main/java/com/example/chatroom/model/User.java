package com.example.chatroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "[user]", schema = "dbo")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "hash_password")
    private String hashPassword;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "status")
    private Boolean status;//true = online, false = offline

    @Column(nullable = false, name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(nullable = true, name = "profile_picture")
    private String profilePicture;//file url

    @JsonIgnore
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Block> blockerUser;

    @JsonIgnore
    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Block> blockerByUser;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactRequest> senderUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactRequest> receiverUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> senderUserMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConversationMember> userConversations;

    @JsonIgnore //prevent recursive result
    @OneToMany(mappedBy = "contactOne", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactWith> contactOneWiths;

    @JsonIgnore
    @OneToMany(mappedBy = "contactTwo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactWith> contactTwoWiths;
}
