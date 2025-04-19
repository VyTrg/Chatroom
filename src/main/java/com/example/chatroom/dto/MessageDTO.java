package com.example.chatroom.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private String content;
    private String senderName;
    private String sectionCode; // mã hóa hoặc thay thế cho sectionId thật
    private LocalDateTime createdAt;
    // Không có trường id thật
}
