package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "message_id",insertable=false, updatable=false)
    private Message message;

    @Column(nullable = false, name = "file_url")
    private String fileUrl;

    @Column(nullable = false, name = "file_type")
    private String fileType;

    @Column(nullable = false, name = "upload_at")
    private LocalDateTime uploadAt;

    public Attachment() {
        this.uploadAt = LocalDateTime.now();
    }
}
