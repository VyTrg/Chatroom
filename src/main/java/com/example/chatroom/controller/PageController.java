package com.example.chatroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PageController {
    @RequestMapping("/home")
    public String home() {
        return "view/home";
    }
    @RequestMapping("/signup")
    public String signup() {
        return "view/sign-up";
    }
    @RequestMapping("/login")
    public String signin() {
        return "view/sign-in";
    }
}
