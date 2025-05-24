package com.example.chatroom.service;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ContactWithRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactWithServiceImpl implements ContactWithService{
    @Autowired
    private ContactWithRepository contactWithRepository;

    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ContactWith> getAllContactsWith() {
        return contactWithRepository.findAll();
    }

    @Override
    public ContactWith getContactWithByUserId(Long id) {
        return contactWithRepository.findContactWithsByContactOne_Id(id);
    }

    @Override
    public ContactWith saveContactWith(ContactWith contactWith) {
        // Không cho phép tự kết bạn với chính mình
        if (contactWith.getContactOne().getId().equals(contactWith.getContactTwo().getId())) {
            throw new IllegalArgumentException("Không thể gửi yêu cầu kết bạn cho chính mình.");
        }
        // Kiểm tra đã tồn tại mối quan hệ bạn bè hay chưa (theo cả 2 chiều)
        ContactWith existing1 = contactWithRepository.findContactWithsByContactOne_IdAndContactTwo_Id(contactWith.getContactOne().getId(), contactWith.getContactTwo().getId());
        ContactWith existing2 = contactWithRepository.findContactWithsByContactOne_IdAndContactTwo_Id(contactWith.getContactTwo().getId(), contactWith.getContactOne().getId());
        if (existing1 != null || existing2 != null) {
            throw new IllegalArgumentException("Hai người dùng đã là bạn bè hoặc đã gửi yêu cầu trước đó.");
        }
        // Có thể kiểm tra thêm user tồn tại nếu cần
        return contactWithRepository.save(contactWith);
    }

    @Override
    public ContactWith updateContactWith(ContactWith contactWith) {
        return contactWithRepository.save(contactWith);
    }

    @Override
    public void deleteContactWith(ContactWith contactWith) {
        contactWithRepository.delete(contactWith);
    }

    @Override
    public ContactWith getContactWithById(Long id) {
        Optional<ContactWith> contactWithOptional = contactWithRepository.findById(id);
        return contactWithOptional.orElse(null);
    }
    
    @Override
    public ContactWith getContactWithByUserIds(Long userId1, Long userId2) {
        // Kiểm tra tìm kiếm theo cả hai hướng
        ContactWith contact = contactWithRepository.findContactWithsByContactOne_IdAndContactTwo_Id(userId1, userId2);
        if (contact != null) {
            return contact;
        }
        
        // Nếu không tìm thấy theo hướng thứ nhất, thử hướng thứ hai
        return contactWithRepository.findContactWithsByContactOne_IdAndContactTwo_Id(userId2, userId1);
    }
}
