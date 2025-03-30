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

<<<<<<< HEAD
    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "contact_one_id",insertable=false, updatable=false)
    private User contactOne;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "contact_two_id",insertable=false, updatable=false)
=======
    @ManyToOne(cascade=CascadeType.ALL, optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name = "contact_one_id")
    private User contactOne;

    @ManyToOne(cascade=CascadeType.ALL, optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name = "contact_two_id")
>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
    private User contactTwo;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    public ContactWith() {
        this.createdAt = LocalDateTime.now();
    }
<<<<<<< HEAD
=======


>>>>>>> a22ce46 (chatroom Huy moi lam dang ky dang nhap co giao dien)
}
