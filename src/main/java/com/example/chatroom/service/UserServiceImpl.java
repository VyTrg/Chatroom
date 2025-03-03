package com.example.chatroom.service;



import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.mapper.UserWithContactsMapper;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.exception.ApiRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    private final UserWithContactsMapper userWithContactsMapper = new UserWithContactsMapper();


//    private final UserWithContactsMapper userWithContactsMapper = new UserWithContactsMapper();


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


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiRequestException("User With id=" + id + " Not Found"));
    }

    @Override

    public UserWithContactsDTO getUserWithContactsDTOById(Long id) {
        UserWithContactsDTO userWithContactsDTO = new UserWithContactsDTO();
        User user = userRepository.findById(id).get();
        List<ContactWith> contactList = userRepository.findAllContacts(id);
        userWithContactsDTO = userWithContactsMapper.toUserWithContactsDTO(user, contactList);
        return userWithContactsDTO;
    }
}
