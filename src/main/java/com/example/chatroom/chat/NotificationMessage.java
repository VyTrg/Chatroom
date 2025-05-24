package com.example.chatroom.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private MessageType type;
    private String message;
    private Long userId;
}
