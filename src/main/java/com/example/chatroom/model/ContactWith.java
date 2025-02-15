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
public class ContactWith {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL, optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name = "contact_one_id")
    private User contactOne;

    @ManyToOne(cascade=CascadeType.ALL, optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name = "contact_two_id")
    private User contactTwo;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    public ContactWith() {
        this.createdAt = LocalDateTime.now();
    }


}
