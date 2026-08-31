package com.example.movieproject.domain.account.service;

import com.example.movieproject.common.exception.CustomException;
import com.example.movieproject.common.exception.ErrorCode;
import com.example.movieproject.domain.account.dto.LoginRequest;
import com.example.movieproject.domain.account.dto.SignUpRequest;
import com.example.movieproject.domain.account.dto.TokenResponse;
import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import com.example.movieproject.security.jwt.JwtTokenProvider;
import com.example.movieproject.security.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // 반드시 암호화해서 저장
                .email(request.email())
                .nickname(request.nickname())
                .build();

        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(user);
    }

    @Override
    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        // Redis에 저장된 값과 일치하는지 확인 — 탈취/재사용 방지
        if (!refreshTokenService.isValid(email, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        return issueTokens(user);
    }

    @Override
    public void logout(String email) {
        refreshTokenService.delete(email);
    }

    // Access + Refresh 토큰 발급 및 Redis 저장을 한 곳에 모은 private 메서드
    private TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        refreshTokenService.save(user.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenValidity());

        return new TokenResponse(accessToken, refreshToken);
    }
}