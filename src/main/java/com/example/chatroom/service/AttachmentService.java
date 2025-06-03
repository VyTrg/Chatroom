package com.example.chatroom.service;

import com.example.chatroom.model.Attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface AttachmentService {
    Attachment uploadAttachment(MultipartFile file, Long messageId) throws IOException;

    Attachment getAttachmentsByMessageId(Long messageId);

    Attachment sendAttachment(Long senderId, Long conversationId, MultipartFile file);
    
    /**
     * Xóa tất cả tệp đính kèm thuộc về một cuộc trò chuyện
     * @param conversationId ID của cuộc trò chuyện
     */
    void deleteAttachmentsByConversationId(Long conversationId);
    
    /**
     * Xóa tất cả tệp đính kèm thuộc về danh sách tin nhắn
     * @param messageIds Danh sách ID tin nhắn
     */
    void deleteAttachmentsByMessageIds(List<Long> messageIds);
}
