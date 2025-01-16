package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
//@Table(name="block")
@Getter
@Setter
@NoArgsConstructor
public class blockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel blocker;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel blocked;

    @Column(name="create_at")
    private LocalDateTime createAt;
}
