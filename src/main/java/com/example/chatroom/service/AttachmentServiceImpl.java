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
    public List<Attachment> getAttachmentsByMessageId(Long messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }

    @Override
    public Attachment sendAttachment(Long senderId, Long conversationId, MultipartFile file) {
        // Create and populate the Message entity
        Message message = new Message();
        message.setMessageText(null);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        message.setSender(sender);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));// Create user reference
        message.setConversation(conversation); // Create conversation reference
        message.setIsRead(false); // Default unread status
        message.setCreatedAt(LocalDateTime.now());

        // Save the Message entity
        Message savedMessage = messageRepository.save(message);

        // Upload the file to Cloudinary
        Map<?, ?> uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "attachment"
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create and save the Attachment entity linked to the saved message
        Attachment attachment = new Attachment();
        attachment.setMessage(savedMessage);
        attachment.setFileUrl(uploadResult.get("secure_url").toString());
        attachment.setFileType(file.getContentType());
        attachment.setUploadAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }
}
