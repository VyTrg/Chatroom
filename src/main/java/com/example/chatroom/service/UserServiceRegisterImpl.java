package com.example.chatroom.service;

import com.example.chatroom.dto.UserDto;
import com.example.chatroom.exception.UserAlreadyExistException;
import com.example.chatroom.model.User;
import com.example.chatroom.model.VerificationToken;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceRegisterImpl implements UserServiceRegister {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        // Kiểm tra xem email đã tồn tại chưa
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("Có một tài khoản với địa chỉ email: " + userDto.getEmail());
        }

        // Kiểm tra xem username đã tồn tại chưa
        if (usernameExists(userDto.getUsername())) {
            throw new UserAlreadyExistException("Tên đăng nhập đã tồn tại: " + userDto.getUsername());
        }

        // Tạo đối tượng User mới
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setHashPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setStatus(false);
        user.setLastSeen(LocalDateTime.now());
        user.setEnabled(false);

        // Lưu User vào database
        User savedUser = userRepository.save(user);

        // Tạo token xác minh email
        String token = UUID.randomUUID().toString();
        createVerificationToken(savedUser, token);

        // Gửi email xác nhận
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        try {
            emailService.sendEmail(savedUser.getEmail(), "Xác minh tài khoản của bạn",
                    "Click vào link sau để xác minh tài khoản: " + verificationLink);
        } catch (Exception e) {
            System.out.println("Không gửi được email xác minh: " + e.getMessage());
        }

        return savedUser;
    }

    @Override
    public String registerUser(UserDto userDto) {
        try {
            registerNewUserAccount(userDto);
            return "Đăng ký thành công! Vui lòng kiểm tra email của bạn để xác minh.";
        } catch (UserAlreadyExistException e) {
            return e.getMessage();
        }
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Mã thông báo xác minh không hợp lệ!";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.deleteById(verificationToken.getId());

        return "Tài khoản đã được xác minh thành công!";
    }

    // Kiểm tra email có tồn tại không
    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Kiểm tra username có tồn tại không
    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
