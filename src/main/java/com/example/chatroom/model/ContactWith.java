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
<<<<<<< HEAD

=======
>>>>>>> 656a5f37f2d1e12b1886acd83faa1157c10a0f91
public class ContactWith {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "contact_one_id",insertable=false, updatable=false)
    private User contactOne;

    @ManyToOne(cascade=CascadeType.ALL, optional=true, fetch=FetchType.EAGER)
    @JoinColumn(name = "contact_two_id",insertable=false, updatable=false)
    private User contactTwo;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    public ContactWith() {
        this.createdAt = LocalDateTime.now();
    }
}
