package com.example.chatroom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.chatroom.model.Attachment;
import com.example.chatroom.model.Message;
import com.example.chatroom.repository.AttachmentRepository;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Attachment uploadAttachment(MultipartFile file, Long messageId) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "attachment"
        ));

        Attachment attachment = new Attachment();
        attachment.setFileUrl(uploadResult.get("secure_url").toString());
        attachment.setFileType(file.getContentType());
        attachment.setUploadAt(LocalDateTime.now());

        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        attachment.setMessage(msg);

        return attachmentRepository.save(attachment);
    }

    @Override
    public List<Attachment> getAttachmentsByMessageId(Long messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }
}
