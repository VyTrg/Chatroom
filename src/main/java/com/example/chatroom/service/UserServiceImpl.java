package com.example.chatroom.service;


import com.example.chatroom.dto.UserContactsDTO;
import com.example.chatroom.exception.ApiRequestException;
import com.example.chatroom.mapper.UserContactMapper;
import com.example.chatroom.mapper.UserInforMapper;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ContactWithRepository;
import com.example.chatroom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private final UserContactMapper userContactMapper = new UserContactMapper();

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

//    @Override
//    public User getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow(() -> new ApiRequestException("User With id=" + id + " Not Found"));
//    }


    @Override
    public UserContactsDTO getUserDTOById(Long id) {
        UserContactsDTO userContactsDTO = new UserContactsDTO();
        User user = userRepository.findById(id).get();
        List<ContactWith> contactList = userRepository.findAllContacts(id);
        userContactsDTO = userContactMapper.toUserContactDTO(user, contactList);
        return userContactsDTO;
    }
}
