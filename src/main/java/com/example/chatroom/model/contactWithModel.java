package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="contact_with")
@Getter
@Setter
@NoArgsConstructor
public class contactWithModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @OneToOne(cascade = CascadeType.ALL)
    private userModel contactOne;

//    @OneToOne(cascade = CascadeType.ALL)
    private userModel contactTwo;

    @Column(name="create_at")
    private LocalDateTime createAt;
}
