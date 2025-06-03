package com.example.chatroom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.chatroom.model.Attachment;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.AttachmentRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

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
    public Attachment getAttachmentsByMessageId(Long messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }

    @Override
    public Attachment sendAttachment(Long senderId, Long conversationId, MultipartFile file) {
        Message message = new Message();
        message.setMessageText(null);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        message.setSender(sender);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        message.setConversation(conversation);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        try {
            String publicId = "attachment_" + senderId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "message_attachment",
                    "public_id", publicId,
                    "resource_type", "auto"
            ));

            Attachment attachment = new Attachment();
            attachment.setMessage(savedMessage);
            attachment.setFileUrl(uploadResult.get("secure_url").toString());
            attachment.setFileType(file.getContentType());
            attachment.setUploadAt(LocalDateTime.now());

            return attachmentRepository.save(attachment);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
