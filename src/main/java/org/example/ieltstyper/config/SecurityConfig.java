package org.example.ieltstyper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1. 静态页面
                        .requestMatchers(
                                "/", "/index.html", "/login.html", "/register.html",
                                "/home.html", "/review.html", "/stats.html", "/vip.html",
                                "/dictation.html"
                        ).permitAll()

                        // 2. 静态资源
                        .requestMatchers("/js/**", "/css/**", "/img/**", "/favicon.ico").permitAll()

                        // 3. 登录注册接口
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // 4. 词书列表接口无需登录（必须在 /api/words/** 前面）
                        .requestMatchers("/api/words/books").permitAll()

                        // 5. 其余接口需要 JWT Token
                        .requestMatchers(
                                "/api/auth/user/**",
                                "/api/stats/**",
                                "/api/words/**",
                                "/api/dictation/**"
                        ).authenticated()

                        // 6. 其余所有请求一律要求认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}