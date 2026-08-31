package com.example.movieproject.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// 토큰 발급/검증을 전담하는 클래스
@Component
public class JwtTokenProvider {

    private final SecretKey key; // 서명/검증에 쓰는 비밀키
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    // application.yml의 jwt 설정값을 주입받아 초기화함
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // Access Token 발급, role까지 포함해서 인가 정보도 같이 담음
    public String createAccessToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // Refresh Token 발급, 재발급 용도라 최소 정보만 담음
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 토큰의 subject(email) 추출
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // 서명 위조, 만료 여부 등을 검증함
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 만료됨
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 서명 불일치, 형식 오류 등
        }
    }

    // Redis에 Refresh Token 저장할 때 TTL로 사용함
    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    // 토큰 파싱 + 서명 검증을 한번에 처리하는 내부 메서드
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}