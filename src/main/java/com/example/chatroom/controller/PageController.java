package com.example.chatroom.controller;

import com.example.chatroom.dto.UserDto;
import com.example.chatroom.model.User;
import com.example.chatroom.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String home(Model model) {
        model.addAttribute("includeUserStatusFix", true);
        return "home";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "sign-up";
    }

    @RequestMapping("/login")
    public String signin() {
        return "sign-in";
    }
}
