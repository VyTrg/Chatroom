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

        String requestURI = request.getRequestURI();

        // Gom các endpoint/public resource cần bỏ qua vào 1 mảng để dễ bảo trì
        final String[] skipEndpoints = {
                "/api/auth/login", "/api/auth/register", "/forgot-password", "/reset-password",
                "/api/auth/verify", "/api/users", "/home", "/signup", "/login"
        };

        // Bỏ qua cho WebSocket và static resource
        if (requestURI.startsWith("/ws") ||
            requestURI.startsWith("/css/") || requestURI.startsWith("/js/") ||
            requestURI.startsWith("/img/") || requestURI.startsWith("/fonts/") ||
            java.util.Arrays.stream(skipEndpoints).anyMatch(requestURI::contains)) {
            chain.doFilter(request, response);
            return;
        }

//        if (requestURI.startsWith("/ws")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Bỏ qua yêu cầu cho các API không yêu cầu JWT, ví dụ như login, register, verify, và các endpoint công khai
//        if (requestURI.contains("/api/auth/login") || requestURI.contains("/api/auth/register")
//                || requestURI.contains("/forgot-password") || requestURI.contains("/reset-password")
//                || requestURI.contains("/api/auth/verify") || requestURI.contains("/api/users")
//                || requestURI.contains("/home") || requestURI.contains("/signup") || requestURI.contains("/login")
//                || requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || requestURI.startsWith("/img/") || requestURI.startsWith("/fonts/")) {
//            chain.doFilter(request, response);
//            return;
//        }


        // Lấy token từ header Authorization
        String token = jwtUtil.extractToken(request);

        // Nếu không có token hoặc token đã bị blacklisted, trả về lỗi Unauthorized
        if (token == null || jwtBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Trả về 401 Unauthorized
            response.getWriter().write("Token không hợp lệ hoặc đã bị đăng xuất.");
            return;
        }

        // Nếu token hợp lệ, kiểm tra và lấy thông tin người dùng từ token
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);

            // Nếu có username trong token, tìm thông tin người dùng từ cơ sở dữ liệu
            if (username != null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Kiểm tra null và tạo AuthenticationToken cho người dùng
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // Đặt thông tin người dùng vào SecurityContext để Spring Security xử lý
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (UsernameNotFoundException e) {
                    // logger.error("Người dùng không tồn tại trong database: " + username);
                    // Không throw exception nữa - chỉ log lỗi và tiếp tục
                    // Token sẽ không được xác thực, nhưng filter chain vẫn tiếp tục
                }
            }
        }

        // Tiếp tục chuỗi lọc
        chain.doFilter(request, response);
    }
}
