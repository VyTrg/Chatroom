package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
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
    private LocalDateTime createAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_read")
    private Boolean isRead;

//    @ManyToOne
//    @JoinColumn(name="id")
    private userModel sender;

//    @ManyToOne
//    @JoinColumn(name="id")
    private userModel receiver;
}
