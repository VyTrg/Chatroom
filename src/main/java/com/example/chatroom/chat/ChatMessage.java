package com.example.chatroom.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String senderId;
    private Long groupId;
    private String recipient;
    private String recipientId;
    private String messageId;
    private String timestamp;
    private boolean isMe;
    private String conversationId;
}
