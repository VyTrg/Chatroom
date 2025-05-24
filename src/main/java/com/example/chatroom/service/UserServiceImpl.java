package com.example.chatroom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.chatroom.dto.UserWithContactsDTO;
import com.example.chatroom.mapper.UserWithContactsMapper;
import com.example.chatroom.model.Block;
import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.BlockRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private Cloudinary cloudinary;

    private final UserWithContactsMapper userWithContactsMapper = new UserWithContactsMapper();

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
    public UserWithContactsDTO getUserWithContactsDTOById(Long id) {
        UserWithContactsDTO userWithContactsDTO = new UserWithContactsDTO();
        Optional<User> user = userRepository.findById(id);
        List<ContactWith> contactList = userRepository.findAllContacts(id);
        userWithContactsDTO = userWithContactsMapper.toUserWithContactsDTO(user.orElse(null), contactList);
        System.out.println(userWithContactsDTO.getFirstName() + " " + userWithContactsDTO.getLastName());
        return userWithContactsDTO;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<Conversation> getConversationsOfUser(Long userId) {
        // Gọi repository để lấy tất cả conversation mà user là thành viên
        return userRepository == null ? List.of() :
            conversationRepository.findAllConversationsForUser(userId);
    }

    @Override
    public UserWithContactsDTO getUserWithContactsDTOByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        List<ContactWith> contactList = userRepository.findAllContactsByUsername(username);
        return userWithContactsMapper.toUserWithContactsDTO(user, contactList);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "profile_pictures",
                "public_id", "user_" + userId + "_profile",
                "overwrite", true
        ));

        // Save URL
        String imageUrl = uploadResult.get("secure_url").toString();
        user.setProfilePicture(imageUrl);
        return userRepository.save(user);
    }

    @Override
    public void blockUser(Long userId, Long blockedUserId) {
        // Kiểm tra hai người dùng có tồn tại không
        User blocker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User blocked = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("User to block not found with id: " + blockedUserId));

        // Kiểm tra nếu đã chặn rồi thì không chặn nữa
        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            return; // Đã chặn rồi, không làm gì thêm
        }

        // Tạo bản ghi chặn mới
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);

        // Lưu bản ghi chặn
        blockRepository.save(block);
    }

    @Override
    public User getUserByEmailOrUsernameInDiscussion(String search, Long userId) {
        return userRepository.findByUsernameOrEmailWithInDiscussion(search, userId);
    }

    @Override
    public User findNewContact(String search, Long userId) {
        return userRepository.findByUsernameOrEmailNotInContactWith(search, userId);
    }

}
