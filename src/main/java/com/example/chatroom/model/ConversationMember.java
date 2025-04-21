package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Check(constraints = "role IN ('admin', 'member')")
public class ConversationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
//    @JoinColumn(name = "conversation_id",insertable=false, updatable=false)
    @JoinColumn(name = "conversation_id") // bỏ insertable=false, updatable=false
    private Conversation conversation;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
//    @JoinColumn(name = "user_id",insertable=false, updatable=false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "role")
    private String role;

    @Column(nullable = false, name = "joined_at")
    private LocalDateTime joinedAt;

    public ConversationMember() {
        this.joinedAt = LocalDateTime.now();
    }
}
