package com.example.chatroom.service;

import com.example.chatroom.model.Block;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.BlockRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    private BlockRepository blockRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void unblockUser(Long blocker, Long block) {
        blockRepository.deleteByBlockerAndBlocked(blocker, block);
    }
    
    @Override
    public void blockUser(Long blocker, Long blocked) {
        // Tìm thông tin user từ ID
        User blockerUser = userRepository.findById(blocker)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng chặn"));
        User blockedUser = userRepository.findById(blocked)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng bị chặn"));
        
        // Kiểm tra xem đã tồn tại block chưa
        if (!blockRepository.existsByBlockerAndBlocked(blockerUser, blockedUser)) {
            Block block = new Block();
            block.setBlocker(blockerUser);
            block.setBlocked(blockedUser);
            
            // Lưu block mới
            blockRepository.save(block);
        }
    }
}
