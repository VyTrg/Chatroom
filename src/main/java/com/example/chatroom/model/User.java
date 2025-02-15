package com.example.chatroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
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

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @JsonIgnore
    @Column(nullable = false, name = "hash_password")
    private String hashPassword;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false, name = "status")
    private Boolean status = false; // Mặc định là offline

    @Column(nullable = false, name = "last_seen")
    private LocalDateTime lastSeen = LocalDateTime.now();

    @Column(nullable = true, name = "profile_picture")
    private String profilePicture;

    // Tự động hash password khi đặt giá trị
    public void setPassword(String password) {
        this.hashPassword = new BCryptPasswordEncoder().encode(password);
    }

    // Cập nhật thời gian online khi có thay đổi dữ liệu
    @PrePersist
    @PreUpdate
    public void updateLastSeen() {
        this.lastSeen = LocalDateTime.now();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Block> blockerUser;

    @JsonIgnore
    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Block> blockerByUser;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ContactRequest> senderUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ContactRequest> receiverUserRequest;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Message> senderUserMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ConversationMember> userConversations;

    @JsonIgnore
    @OneToMany(mappedBy = "contactOne", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ContactWith> contactOneWiths;

    @JsonIgnore
    @OneToMany(mappedBy = "contactTwo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ContactWith> contactTwoWiths;
}
