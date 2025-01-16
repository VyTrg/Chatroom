package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
//@Table(name="contact_request")
@Getter
@Setter
@NoArgsConstructor
public class contactRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel sender;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel receiver;

    @Column(name="create_at")
    private LocalDateTime createAt;
}
