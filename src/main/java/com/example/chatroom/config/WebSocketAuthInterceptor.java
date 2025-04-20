package com.example.chatroom.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import java.util.Map;

//public class WebSocketAuthInterceptor implements HandshakeInterceptor {
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        if (request instanceof ServletServerHttpRequest servletRequest) {
//            HttpServletRequest req = (HttpServletRequest) servletRequest.getServletRequest();
//            String token = req.getParameter("token");
//            attributes.put("token", token);
//        }
//        return true;
//    }
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception exception) {}
//}

public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            System.out.println("[WS] Token nhận khi handshake: " + token);
            attributes.put("token", token);
        }
        return true;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}