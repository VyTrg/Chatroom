package com.example.chatroom.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String content;

    private String sender;

    private MessageType type;

    private String recipient; //chat 1-1

    private String groupID; //chat nhóm
}
