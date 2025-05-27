package com.example.chatroom.service;

import com.example.chatroom.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    private BlockRepository blockRepository;

    @Override
    public void unblockUser(Long blocker, Long block) {
        blockRepository.deleteByBlockerAndBlocked(blocker, block);
    }
}
