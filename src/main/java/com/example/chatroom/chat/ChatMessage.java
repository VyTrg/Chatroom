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
    private Long groupId;
    @Setter
    @Getter
    private String recipientId;

    public String getRecipient() {
        return "";
    }
}

