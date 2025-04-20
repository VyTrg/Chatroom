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

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws-plans").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = null;

                    //lấy token trên header "Authorization"
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    }

                    //lấy từ header "access_token"
                    if (token == null) {
                        token = accessor.getFirstNativeHeader("access_token");
                    }

                    //lấy token từ URI query param (WebSocket URL)
                    if (token == null && accessor.getNativeHeader("origin") != null) {
                        String uri = accessor.getNativeHeader("origin").get(0);
                        if (uri != null && uri.contains("access_token=")) {
                            token = uri.substring(uri.indexOf("access_token=") + 13);
                            int amp = token.indexOf('&');
                            if (amp != -1) token = token.substring(0, amp);
                        }
                    }

                    System.out.println("HEADER Authorization: " + authHeader);
                    System.out.println("HEADER access_token: " + accessor.getFirstNativeHeader("access_token"));
                    System.out.println("Token lấy được: " + token);

                    //lấy tken từ session attributes (nếu client truyền qua SockJS)
                    if (token == null && accessor.getSessionAttributes() != null) {
                        Object t = accessor.getSessionAttributes().get("access_token");
                        if (t != null) token = t.toString();
                    }

                    //xác thực và set Principal
                    if (token != null && jwtUtil.validateToken(token)) {
                        String username = jwtUtil.extractUsername(token);
                        System.out.println("WebSocket Principal: " + username);

                        if (username != null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);
                        }
                    } else {
                        System.out.println("WebSocket JWT invalid or missing");
                    }
                }

                return message;
            }
        });
    }
}