package com.example.chatroom.config;

import com.example.chatroom.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
//                .addInterceptors(new WebSocketAuthInterceptor());
        System.out.println("=== [WEBSOCKET_CONFIG] Đã đăng ký endpoint /ws ===");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        System.out.println("=== [WEBSOCKET_CONFIG] Đang cấu hình message broker ===");
        try {
            registry.setApplicationDestinationPrefixes("/app");
            System.out.println("[WEBSOCKET_CONFIG] Đã thiết lập application prefix: /app");

            registry.enableSimpleBroker("/topic", "/queue");
            System.out.println("[WEBSOCKET_CONFIG] Đã kích hoạt broker cho: /topic, /queue");

            System.out.println("[WEBSOCKET_CONFIG] Cấu hình message broker hoàn tất");
        } catch (Exception e) {
            System.err.println("[WEBSOCKET_CONFIG] Lỗi khi cấu hình message broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (accessor.getCommand() == StompCommand.CONNECT) {
                    // Lấy token từ query param hoặc header
                    String token = null;
                    if (accessor.getNativeHeader("token") != null) {
                        token = accessor.getNativeHeader("token").get(0);
                    } else if (accessor.getFirstNativeHeader("Authorization") != null) {
                        token = accessor.getFirstNativeHeader("Authorization");
                        if (token.startsWith("Bearer ")) {
                            token = token.substring(7);
                        }
                    } else if (accessor.getSessionAttributes().get("token") != null) {
                        token = (String) accessor.getSessionAttributes().get("token");
                    }
                    // Nếu vẫn không có, thử lấy từ URI
                    if (token == null && accessor.getHeader("simpConnectMessage") != null) {
                        Message<?> connectMsg = (Message<?>) accessor.getHeader("simpConnectMessage");
                        if (connectMsg != null) {
                            String uri = connectMsg.getHeaders().get("nativeHeaders").toString();
                            // Parse uri để lấy token nếu có
                        }
                    }
                    System.out.println("[WS] Token lấy từ header/query: " + token);
                    // === Thêm đoạn set user xác thực JWT ===
                    if (token != null && !token.isBlank()) {
                        try {
                            String username = jwtUtil.extractUsername(token);
                            if (username != null && jwtUtil.validateToken(token, username)) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                accessor.setUser(auth); // QUAN TRỌNG!
                                System.out.println("[WS] Đã gán user cho session: " + username);
                            } else {
                                System.out.println("[WS] Token không hợp lệ cho user: " + username);
                            }
                        } catch (Exception e) {
                            System.err.println("[WS] Lỗi khi xác thực token: " + e.getMessage());
                        }
                    }
                }
                return message;
            }
        });
    }
}