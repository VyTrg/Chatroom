package com.example.chatroom.service;

import com.example.chatroom.model.ContactWith;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.ContactWithRepository;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactWithServiceImpl implements ContactWithService{
    @Autowired
    private ContactWithRepository contactWithRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
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
        // Lưu liên hệ
        ContactWith saved = contactWithRepository.save(contactWith);
        // Sau khi kết bạn thành công, tự động tạo conversation nếu chưa có
        Long user1Id = contactWith.getContactOne().getId();
        Long user2Id = contactWith.getContactTwo().getId();
        // Kiểm tra đã có hội thoại riêng tư chưa
        Conversation existingConv = conversationRepository.findPrivateConversationBetween(user1Id, user2Id).orElse(null);
        if (existingConv == null) {
            // Tạo hội thoại mới
            Conversation conversation = new Conversation();
            User user1 = userRepository.findById(user1Id).orElseThrow();
            User user2 = userRepository.findById(user2Id).orElseThrow();
            conversation.setName(user1.getUsername() + " & " + user2.getUsername());
            conversation.setIsGroup(false);
            conversationRepository.save(conversation);
            // Thêm 2 thành viên vào conversation_member
            ConversationMember member1 = new ConversationMember();
            member1.setConversation(conversation);
            member1.setUser(user1);
            member1.setRole("member");
            conversationMemberRepository.save(member1);
            ConversationMember member2 = new ConversationMember();
            member2.setConversation(conversation);
            member2.setUser(user2);
            member2.setRole("member");
            conversationMemberRepository.save(member2);
        }
        return saved;
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
        return contactWithRepository.findById(id).get();
    }
}
