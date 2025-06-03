package com.example.chatroom.chat;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private MessageType type;
    private String message;
    private Long userId;
    private Map<String, Object> data;
}
