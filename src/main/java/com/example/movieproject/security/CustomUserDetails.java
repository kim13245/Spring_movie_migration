package com.example.movieproject.security;

import com.example.movieproject.domain.account.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security가 User 엔티티를 인식할 수 있게 감싸는 어댑터 역할
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user; // 실제 도메인 User, Controller에서 꺼내 씀

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 권한 목록 반환, ROLE_ 접두사는 Spring Security 관례임
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 메서드명은 getUsername이지만 로그인 식별자로 email을 씀
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정 잠금/만료 기능 없어서 전부 정상 상태로 고정함
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}