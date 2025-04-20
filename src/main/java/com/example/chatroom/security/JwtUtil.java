package com.example.chatroom.security;

import com.example.chatroom.service.JwtBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final JwtBlacklistService jwtBlacklistService;
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    public JwtUtil(@Value("${jwt.secret}") String secret, JwtBlacklistService jwtBlacklistService) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.jwtBlacklistService = jwtBlacklistService;
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        System.out.println("=== [JWT_UTIL] Đang xác thực token ===");

        if (token == null || token.isEmpty()) {
            System.out.println("[JWT_UTIL] Token rỗng hoặc null");
            return false;
        }

        if (jwtBlacklistService.isBlacklisted(token)) {
            System.out.println("[JWT_UTIL] Token đã bị blacklist");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            String username = extractUsername(token);
            System.out.println("[JWT_UTIL] Token hợp lệ cho user: " + username);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("[JWT_UTIL] Token đã hết hạn: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.err.println("[JWT_UTIL] Lỗi xác thực token: " + e.getMessage());
            return false;
        }
    }

    // Tạo token với thời gian hết hạn tùy chỉnh
    public String generateToken(String username, long expirationTime) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username) {
        return generateToken(username, EXPIRATION_TIME);
    }

    public String extractUsername(String token) {
        System.out.println("[JWT_UTIL] Đang trích xuất username từ token");

        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            System.out.println("[JWT_UTIL] Username trích xuất được: " + username);
            return username;
        } catch (JwtException e) {
            System.err.println("[JWT_UTIL] Lỗi khi trích xuất username: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    // Trích xuất email từ token
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    // Kiểm tra token có hợp lệ không (cho reset password)
    public boolean isTokenValid(String token) {
        return validateToken(token);
    }
}
