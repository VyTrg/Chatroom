package com.example.chatroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Value("${security.password.encoder.strength:10}")
    private int strength;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cấu hình CSRF
//        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.csrf(AbstractHttpConfigurer::disable);  // Tắt CSRF để API có thể nhận request từ Postman
        // Cấu hình các yêu cầu và bảo mật
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/registrationConfirm").permitAll() // Cho phép đăng ký mà không cần xác thực
                        .anyRequest().authenticated()) // Bảo mật tất cả các yêu cầu còn lại
                .formLogin(Customizer.withDefaults()); // cấu hình form login
//                http.formLogin(AbstractHttpConfigurer::disable);// ❌ Tắt form login mặc định
        return http.build();
    }
}
