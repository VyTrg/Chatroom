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

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
//        registry.addEndpoint("/ws-plans").setAllowedOriginPatterns("*").withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.enableSimpleBroker("/topic", "/queue");
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String token = null;
//
//                    //lấy token trên header "Authorization"
//                    String authHeader = accessor.getFirstNativeHeader("Authorization");
//                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                        token = authHeader.substring(7);
//                    }
//
//                    //lấy từ header "access_token"
//                    if (token == null) {
//                        token = accessor.getFirstNativeHeader("access_token");
//                    }
//
//                    //lấy token từ URI query param (WebSocket URL)
//                    if (token == null && accessor.getNativeHeader("origin") != null) {
//                        String uri = accessor.getNativeHeader("origin").get(0);
//                        if (uri != null && uri.contains("access_token=")) {
//                            token = uri.substring(uri.indexOf("access_token=") + 13);
//                            int amp = token.indexOf('&');
//                            if (amp != -1) token = token.substring(0, amp);
//                        }
//                    }
//
//                    System.out.println("HEADER Authorization: " + authHeader);
//                    System.out.println("HEADER access_token: " + accessor.getFirstNativeHeader("access_token"));
//                    System.out.println("Token lấy được: " + token);
//
//                    //lấy tken từ session attributes (nếu client truyền qua SockJS)
//                    if (token == null && accessor.getSessionAttributes() != null) {
//                        Object t = accessor.getSessionAttributes().get("access_token");
//                        if (t != null) token = t.toString();
//                    }
//
//                    //xác thực và set Principal
//                    if (token != null && jwtUtil.validateToken(token)) {
//                        String username = jwtUtil.extractUsername(token);
//                        System.out.println("WebSocket Principal: " + username);
//
//                        if (username != null) {
//                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//                            UsernamePasswordAuthenticationToken authentication =
//                                    new UsernamePasswordAuthenticationToken(
//                                            userDetails,
//                                            null,
//                                            userDetails.getAuthorities()
//                                    );
//
//                            SecurityContextHolder.getContext().setAuthentication(authentication);
//                            accessor.setUser(authentication);
//                        }
//                    } else {
//                        System.out.println("WebSocket JWT invalid or missing");
//                    }
//                }
//
//                return message;
//            }
//        });
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("=== [WEBSOCKET_CONFIG] Đang đăng ký các endpoint WebSocket ===");
        try {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
            System.out.println("[WEBSOCKET_CONFIG] Đã đăng ký endpoint /ws with SockJS");

            registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
            System.out.println("[WEBSOCKET_CONFIG] Đã đăng ký endpoint /ws without SockJS");

            registry.addEndpoint("/ws-plans").setAllowedOriginPatterns("*").withSockJS();
            System.out.println("[WEBSOCKET_CONFIG] Đã đăng ký endpoint /ws-plans");

            System.out.println("[WEBSOCKET_CONFIG] Tất cả endpoint đã đăng ký thành công");
        } catch (Exception e) {
            System.err.println("[WEBSOCKET_CONFIG] Lỗi khi đăng ký endpoint: " + e.getMessage());
            e.printStackTrace();
        }
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
        System.out.println("=== [WEBSOCKET_CONFIG] Đang cấu hình kênh đầu vào ===");

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("[WEBSOCKET_CONFIG] Xử lý tin nhắn đến: " + message);
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null) {
                    System.out.println("[WEBSOCKET_CONFIG] StompCommand: " + accessor.getCommand());

                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        System.out.println("[WEBSOCKET_CONFIG] Xử lý CONNECT command");
                        String token = null;

                        // Lấy token từ header "Authorization"
                        String authHeader = accessor.getFirstNativeHeader("Authorization");
                        System.out.println("[WEBSOCKET_CONFIG] Authorization header: " + authHeader);
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            token = authHeader.substring(7);
                            System.out.println("[WEBSOCKET_CONFIG] Token từ Authorization: " + token);
                        }

                        // Lấy từ header "access_token"
                        if (token == null) {
                            token = accessor.getFirstNativeHeader("access_token");
                            System.out.println("[WEBSOCKET_CONFIG] Token từ access_token header: " + token);
                        }

                        // Lấy token từ URI query param
                        if (token == null && accessor.getNativeHeader("origin") != null) {
                            String uri = accessor.getNativeHeader("origin").get(0);
                            System.out.println("[WEBSOCKET_CONFIG] URI origin: " + uri);
                            if (uri != null && uri.contains("access_token=")) {
                                token = uri.substring(uri.indexOf("access_token=") + 13);
                                int amp = token.indexOf('&');
                                if (amp != -1) token = token.substring(0, amp);
                                System.out.println("[WEBSOCKET_CONFIG] Token từ URI param: " + token);
                            }
                        }

                        // Lấy token từ session attributes
                        if (token == null && accessor.getSessionAttributes() != null) {
                            Object t = accessor.getSessionAttributes().get("access_token");
                            if (t != null) {
                                token = t.toString();
                                System.out.println("[WEBSOCKET_CONFIG] Token từ session: " + token);
                            }
                        }

                        // Xác thực token
                        System.out.println("[WEBSOCKET_CONFIG] Token cuối cùng để xác thực: " + token);

                        try {
                            if (token != null && jwtUtil.validateToken(token)) {
                                String username = jwtUtil.extractUsername(token);
                                System.out.println("[WEBSOCKET_CONFIG] Người dùng hợp lệ: " + username);

                                if (username != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                    System.out.println("[WEBSOCKET_CONFIG] Đã tải UserDetails: " + userDetails.getUsername());

                                    UsernamePasswordAuthenticationToken authentication =
                                            new UsernamePasswordAuthenticationToken(
                                                    userDetails,
                                                    null,
                                                    userDetails.getAuthorities()
                                            );

                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    accessor.setUser(authentication);
                                    System.out.println("[WEBSOCKET_CONFIG] Đã thiết lập User cho accessor: " + authentication.getName());
                                }
                            } else {
                                System.out.println("[WEBSOCKET_CONFIG] Token không hợp lệ hoặc không có");
                                // Để test, có thể thêm user mặc định
                                // accessor.setUser(() -> "test-user");
                                // System.out.println("[WEBSOCKET_CONFIG] Đã thiết lập test-user cho development");
                            }
                        } catch (Exception e) {
                            System.err.println("[WEBSOCKET_CONFIG] Lỗi khi xác thực token: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                        System.out.println("[WEBSOCKET_CONFIG] SUBSCRIBE Command");
                        System.out.println("[WEBSOCKET_CONFIG] Destination: " + accessor.getDestination());
                        System.out.println("[WEBSOCKET_CONFIG] User: " + (accessor.getUser() != null ? accessor.getUser().getName() : "null"));
                    } else if (StompCommand.SEND.equals(accessor.getCommand())) {
                        System.out.println("[WEBSOCKET_CONFIG] SEND Command");
                        System.out.println("[WEBSOCKET_CONFIG] Destination: " + accessor.getDestination());
                        System.out.println("[WEBSOCKET_CONFIG] User: " + (accessor.getUser() != null ? accessor.getUser().getName() : "null"));
                    }
                }

                return message;
            }
        });

        System.out.println("[WEBSOCKET_CONFIG] Cấu hình kênh đầu vào hoàn tất");
    }
}
