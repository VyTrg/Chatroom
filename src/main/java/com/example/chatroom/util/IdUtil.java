package com.example.chatroom.util;

import java.util.Base64;

public class IdUtil {
    public static String encodeSectionId(Long sectionId) {
        return Base64.getUrlEncoder().encodeToString(sectionId.toString().getBytes());
    }
    public static Long decodeSectionCode(String sectionCode) {
        return Long.parseLong(new String(Base64.getUrlDecoder().decode(sectionCode)));
    }
}