package com.example.movieproject.config;

import com.example.movieproject.security.CustomUserDetailsService;
import com.example.movieproject.security.jwt.JwtAuthenticationEntryPoint;
import com.example.movieproject.security.jwt.JwtAuthenticationFilter;
import com.example.movieproject.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 지금까지 만든 컴포넌트들을 필터체인에 등록하는 최종 설정
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // 비밀번호 암호화에 BCrypt 사용함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 나중에 확장(소셜 로그인 등) 대비해서 미리 열어둠, 지금은 직접 안 씀
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 세션 기반이 아니라서 CSRF 공격 자체가 성립 안 함
                .csrf(csrf -> csrf.disable())
                // 세션을 아예 안 만듦, Stateless 철학의 핵심 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증 실패 시 처리할 EntryPoint 등록
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // 경로별 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // 회원가입/로그인은 누구나
                        .requestMatchers("/api/movies/**").permitAll() // 영화 조회는 비로그인도 가능
                        .anyRequest().authenticated()                   // 나머지는 인증 필요
                )
                // Spring 기본 필터보다 먼저 우리 JWT 필터가 실행되게 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}