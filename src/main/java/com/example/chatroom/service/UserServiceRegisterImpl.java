package com.example.chatroom.service;

import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceRegisterImpl implements UserServiceRegister {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public String registerUser(User user) {
        // Kiểm tra nếu email đã tồn tại
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "Email đã tồn tại!";
        }

        // Mã hóa mật khẩu từ thuộc tính password và lưu vào hashPassword
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setHashPassword(encodedPassword); // Lưu hashPassword

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);

        return "Đăng ký thành công!";
    }
}
