package com.example.chatroom.controller;

import com.example.chatroom.model.Attachment;
import com.example.chatroom.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
    @RequestMapping("/api/attachments")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<Attachment> uploadFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("messageId") Long messageId) {
        try {
            Attachment uploaded = attachmentService.uploadAttachment(file, messageId);
            return ResponseEntity.ok(uploaded);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<Attachment> getAttachments(@PathVariable Long messageId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByMessageId(messageId));
    }

    @PostMapping("/send")
    public ResponseEntity<Attachment> sendAttachment(
            @RequestParam("senderId") Long senderId,
            @RequestParam("conversationId") Long conversationId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            Attachment attachment = attachmentService.sendAttachment(senderId, conversationId, file);
            return ResponseEntity.ok(attachment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
