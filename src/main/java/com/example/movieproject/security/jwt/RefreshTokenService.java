package com.example.movieproject.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

// Refresh Token을 Redis에 저장/조회/삭제하는 역할
// Access Token과 다르게 서버가 상태를 직접 관리함 (Stateful)
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "refresh:";

    // key: refresh:{email}, TTL 지나면 Redis가 자동으로 삭제함
    public void save(String email, String refreshToken, long validityMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + email,
                refreshToken,
                Duration.ofMillis(validityMillis)
        );
    }

    public String find(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    // 로그아웃 시 호출, 삭제되면 해당 토큰으로 재발급 불가능함
    public void delete(String email) {
        redisTemplate.delete(PREFIX + email);
    }

    // 클라이언트가 보낸 토큰과 저장된 값이 일치하는지 확인함
    public boolean isValid(String email, String refreshToken) {
        String stored = find(email);
        return stored != null && stored.equals(refreshToken);
    }
}