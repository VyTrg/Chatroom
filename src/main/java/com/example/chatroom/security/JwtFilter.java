package com.example.chatroom.security;

import com.example.chatroom.service.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtBlacklistService jwtBlacklistService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, JwtBlacklistService jwtBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        final String[] skipEndpoints = {
                "/api/auth/login", "/api/auth/register", "/forgot-password", "/reset-password",
                "/api/auth/verify", "/api/users", "/home", "/signup", "/login"
        };

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) ||
                path.startsWith("/ws") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/img/") || path.startsWith("/fonts/") ||
                java.util.Arrays.stream(skipEndpoints).anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.extractToken(request);

        if (token == null || jwtBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token không hợp lệ hoặc đã bị đăng xuất.");
            return;
        }

        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (UsernameNotFoundException e) {
                    // Log nếu cần
                }
            }
        }

        chain.doFilter(request, response);
    }

}
