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

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;// true or false

    @JsonIgnore
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blockerUser;

    @JsonIgnore
    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blockerByUser;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactRequest> senderUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactRequest> receiverUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> senderUserMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationMember> userConversations;

    @JsonIgnore
    @OneToMany(mappedBy = "contactOne", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactWith> contactOne;

    @JsonIgnore
    @OneToMany(mappedBy = "contactTwo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactWith> contactTwo;

    public User() {
        this.enabled = false;
    }
}
