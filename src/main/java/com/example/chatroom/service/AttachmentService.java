package com.example.chatroom.service;

import com.example.chatroom.model.Attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface AttachmentService {
    Attachment uploadAttachment(MultipartFile file, Long messageId) throws IOException;

    List<Attachment> getAttachmentsByMessageId(Long messageId);

    Attachment sendAttachment(Long senderId, Long conversationId, MultipartFile file);
}
