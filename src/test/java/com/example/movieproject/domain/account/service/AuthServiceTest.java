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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        SignUpRequest request = new SignUpRequest(
                "testuser",
                "test@example.com",
                "password123",
                "테스트유저"
        );

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

        // when
        authService.signUp(request);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_Fail_DuplicateEmail() {
        // given
        SignUpRequest request = new SignUpRequest(
                "testuser",
                "test@example.com",
                "password123",
                "테스트유저"
        );

        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 중복")
    void signUp_Fail_DuplicateUsername() {
        // given
        SignUpRequest request = new SignUpRequest(
                "testuser",
                "test@example.com",
                "password123",
                "테스트유저"
        );

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_USERNAME);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        long refreshTokenValidity = 1000L * 60 * 60 * 24 * 14;

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(user.getEmail())).willReturn("refreshToken");
        given(jwtTokenProvider.getRefreshTokenValidity()).willReturn(refreshTokenValidity);

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
        verify(refreshTokenService).save(user.getEmail(), "refreshToken", refreshTokenValidity);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_Fail_UserNotFound() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_InvalidPassword() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴한 계정")
    void login_Fail_DeactivatedAccount() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();
        user.deactivate();

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCOUNT_DEACTIVATED);
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_Success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        long refreshTokenValidity = 1000L * 60 * 60 * 24 * 14;

        User user = User.builder()
                .email(email)
                .username("testuser")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmail(refreshToken)).willReturn(email);
        given(refreshTokenService.isValid(email, refreshToken)).willReturn(true);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createAccessToken(email, user.getRole().name())).willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(email)).willReturn("newRefreshToken");
        given(jwtTokenProvider.getRefreshTokenValidity()).willReturn(refreshTokenValidity);

        // when
        TokenResponse response = authService.reissue(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        assertThat(response.refreshToken()).isEqualTo("newRefreshToken");
        verify(refreshTokenService).save(email, "newRefreshToken", refreshTokenValidity);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 토큰")
    void reissue_Fail_InvalidToken() {
        // given
        String refreshToken = "invalidRefreshToken";
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // given
        String email = "test@example.com";

        // when
        authService.logout(email);

        // then
        verify(refreshTokenService).delete(email);
    }
}
