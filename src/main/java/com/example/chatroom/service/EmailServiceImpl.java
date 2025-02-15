package com.example.chatroom.service;

import com.example.chatroom.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendVerificationEmail(String to, String confirmationUrl) {
        System.out.println("Đang gửi email đến: " + to);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Xác nhận đăng ký tài khoản");
            helper.setText("<p>Nhấp vào liên kết sau để xác nhận tài khoản của bạn:</p>" +
                    "<a href='" + confirmationUrl + "'>Xác nhận tài khoản</a>", true);

            mailSender.send(message);
            System.out.println("Email đã gửi thành công đến: " + to);
        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
            throw new RuntimeException("Gửi email thất bại!", e);
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println(" Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println(" Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

