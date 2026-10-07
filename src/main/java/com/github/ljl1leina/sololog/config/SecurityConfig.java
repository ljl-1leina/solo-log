package com.github.ljl1leina.sololog.config;

import com.github.ljl1leina.sololog.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                       // 前后端分离+JWT，不需要CSRF防护
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 不用session
                .formLogin(AbstractHttpConfigurer::disable)                 // 不要Security自带的登录页
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 白名单：谁都能访问
                        .requestMatchers(
                                "/api/auth/**",
                                "/hello/**", "/text/**",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/likes/**").authenticated()
                        // 评论：看不需要登录，写/删需要
                        .requestMatchers(HttpMethod.GET, "/api/posts/comments/**").permitAll()   // 已被 GET /api/posts/** 兜住，显式写出意图
                        .requestMatchers(HttpMethod.POST, "/api/posts/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                        // 我的资料：需登录（是谁由token决定，URL里没有id）
                        .requestMatchers("/api/me/**").authenticated()
                        // 头像库列表：放行（注册页等未登录场景也要能看）
                        .requestMatchers("/api/avatars/**").permitAll()
                        // 头像静态图片：放行（否则 anyRequest 会把 /avatars/1.png 拦成401）
                        .requestMatchers("/avatars/**").permitAll()
                        // 游客可读文章
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        // 文章的写操作：仅AUTHOR
                        .requestMatchers(HttpMethod.POST, "/api/posts").hasRole("AUTHOR")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("AUTHOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("AUTHOR")
                        // 其余一律要登录
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
