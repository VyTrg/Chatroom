package com.example.chatroom.service;

public interface BlockService {
    void unblockUser(Long blocker, Long block);
    void blockUser(Long blocker, Long blocked);
}
