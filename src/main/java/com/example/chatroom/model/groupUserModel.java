package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name="group_user")
@Getter
@Setter
@NoArgsConstructor
public class groupUserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private groupModel group;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel user;
}
