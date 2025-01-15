package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="block")
@Getter
@Setter
@NoArgsConstructor
public class messageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "create_at")
    private java.time.LocalDateTime createAt;

    @Column(name = "delete_at")
    private java.time.LocalDateTime deleteAt;

    @Column(name = "update_at")
    private java.time.LocalDateTime updateAt;

    @Column(name = "is_read")
    private Boolean isRead;

    @OneToMany
    private userModel sender;

    @OneToMany
    private receiverModel receiver;
}
