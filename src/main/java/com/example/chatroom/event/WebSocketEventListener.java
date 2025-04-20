//package com.example.chatroom.event;
//
//import com.example.chatroom.chat.ChatMessage;
//import com.example.chatroom.chat.MessageType;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.Map;
//import java.util.Objects;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WebSocketEventListener {
//
//    private final SimpMessageSendingOperations messageTemplate;
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(
//            SessionDisconnectEvent event
//    ) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

import com.example.chatroom.chat.ChatMessage;
import com.example.chatroom.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

////        String username = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username").toString();
//        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
//        if (sessionAttributes == null) {
//            // Có thể log hoặc bỏ qua, hoặc return
//            return;
//        }
//        Object usernameObj = sessionAttributes.get("username");
//        if (usernameObj == null) {
//            // Có thể log hoặc bỏ qua, hoặc return
//            return;
//        }
//        String username = usernameObj.toString();
//        if (username != null) {
//            log.info("Disconnected from {}", username);
//            var chatMessage = ChatMessage.builder()
//                    .type(MessageType.LEAVE)
//                    .sender(username)
//                    .build();
//            messageTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }
//}
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("=== [WEBSOCKET_EVENT] Sự kiện ngắt kết nối WebSocket ===");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("[WEBSOCKET_EVENT] SessionId: " + headerAccessor.getSessionId());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            System.out.println("[WEBSOCKET_EVENT] SessionAttributes là null");
            return;
        }

        Object usernameObj = sessionAttributes.get("username");
        if (usernameObj == null) {
            System.out.println("[WEBSOCKET_EVENT] Không tìm thấy username trong session");
            return;
        }

        String username = usernameObj.toString();
        System.out.println("[WEBSOCKET_EVENT] Người dùng ngắt kết nối: " + username);

        try {
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            System.out.println("[WEBSOCKET_EVENT] Gửi thông báo rời đi tới /topic/public");
            messageTemplate.convertAndSend("/topic/public", chatMessage);
            System.out.println("[WEBSOCKET_EVENT] Đã gửi thông báo thành công");
        } catch (Exception e) {
            System.err.println("[WEBSOCKET_EVENT] Lỗi khi gửi thông báo ngắt kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        System.out.println("=== [WEBSOCKET_EVENT] Sự kiện kết nối WebSocket ===");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("[WEBSOCKET_EVENT] SessionId kết nối: " + headerAccessor.getSessionId());

        if (headerAccessor.getUser() != null) {
            System.out.println("[WEBSOCKET_EVENT] User kết nối: " + headerAccessor.getUser().getName());
        } else {
            System.out.println("[WEBSOCKET_EVENT] Kết nối không có thông tin user");
        }

        System.out.println("[WEBSOCKET_EVENT] Các header: " + headerAccessor.toNativeHeaderMap());
    }
}
