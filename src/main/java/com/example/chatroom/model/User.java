package com.example.chatroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
<<<<<<< HEAD
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

=======
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
<<<<<<< HEAD
@NoArgsConstructor
=======

>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
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

<<<<<<< HEAD
=======
    @Email
    @NotNull
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "status")
    private Boolean status;//true = online, false = offline

    @Column(nullable = false, name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(nullable = true, name = "profile_picture")
    private String profilePicture;//file url

<<<<<<< HEAD
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
=======
    @Column(nullable = false, name = "enabled")
    private Boolean enabled;// true or false

    @JsonIgnore
    @Transient
    private String password;


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

    public User(String firstName, String lastName, String username, String password, String email, Boolean status, LocalDateTime lastSeen, String profilePicture, boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.setPassword(password); // Dùng setter để hash mật khẩu
        this.email = email;
        this.status = status;
        this.lastSeen = lastSeen;
        this.profilePicture = profilePicture;
        this.enabled = enabled;
    }


    // Hash password trước khi lưu vào database
    public void setPassword(String password) {
        this.password = password;
        this.hashPassword = hashPassword(password);
    }

    // Tự động hash mật khẩu trước khi lưu hoặc cập nhật
    @PrePersist
    @PreUpdate
    private void preSave() {
        if (this.password != null) {
            this.hashPassword = hashPassword(this.password);
        }
    }

    // Hash mật khẩu bằng BCrypt
    private String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}

>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
