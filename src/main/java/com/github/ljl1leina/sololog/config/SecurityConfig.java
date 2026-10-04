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
