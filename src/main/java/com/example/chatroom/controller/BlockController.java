package com.example.chatroom.controller;

import com.example.chatroom.service.BlockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api")
public class BlockController {

    @Autowired
    private BlockServiceImpl blockService;

    @DeleteMapping("/unblock")
    public ResponseEntity<Void> ubBlockUser(@RequestParam("blocker") Long blocker, @RequestParam("blocked") Long blocked) {
        blockService.unblockUser(blocker, blocked);
        return ResponseEntity.ok().build();
    }
}
