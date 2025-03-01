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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceRegisterImpl implements UserServiceRegister {

    @Autowired //thao tác với database
    private UserRepository userRepository;

    @Autowired //lưu và truy xuất token
    private VerificationTokenRepository tokenRepository;

    @Autowired //gửi mail xác nhận
    private EmailService emailService;

    @Autowired //mã hóa mật khẩu
    private PasswordEncoder passwordEncoder;

    @Override //đăng ký tài khoản cho người mới
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("Có một tài khoản với địa chỉ email: " + userDto.getEmail());//kiểm tra xem email đã tồn tại trong database hay chưa
        }

        //tạo đối tượng User mới
        User user = new User();
        user.setFirstName(userDto.getFirstName());//lưu tên
        user.setLastName(userDto.getLastName());//lưu họ
        user.setUsername(userDto.getUsername());//lưu tên người dùng
        user.setEmail(userDto.getEmail());//lưu email
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); //mã hóa mật khẩu trước khi lưu
        user.setStatus(false);//đặt trạng thái offline
        user.setLastSeen(LocalDateTime.now());//lưu thời gian hoạt động gần nhất của user
        user.setEnabled(false);// tài khoản chưa kích hoạt

        //lưu user vaof database
        User savedUser = userRepository.save(user);

        // Tạo token và gửi email xác nhận
        String token = UUID.randomUUID().toString();//random 1 token bất kỳ
        createVerificationToken(savedUser, token);//lưu token vào database

        //tạo link để xác minh email
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        try {
            //gửi email xác minh tài khoản
            emailService.sendEmail(savedUser.getEmail(), "Xác minh tài khoản của bạn", "Click here to verify: " + verificationLink);
        } catch (Exception e) {
            System.out.println("Không gửi được email xác minh: " + e.getMessage());
        }

        return savedUser; //trả về user đã đăng ký
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

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
