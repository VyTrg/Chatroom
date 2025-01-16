package com.example.chatroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
//@Table(name="group")
@Getter
@Setter
@NoArgsConstructor
public class groupModel extends receiverModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="group_name")
    private String groupName;

    @Column(name="create_at")
    private LocalDateTime createAt;

    @OneToOne(cascade = CascadeType.ALL)
    private userModel createBy;
}
