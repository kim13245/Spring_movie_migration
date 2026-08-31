package com.example.movieproject.security;

import com.example.movieproject.domain.account.entity.User;
import com.example.movieproject.domain.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security가 인증 시점에 사용자 조회를 위임하는 인터페이스의 구현체
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 파라미터명은 username이지만 실제로는 email이 들어옴
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음: " + email));
        return new CustomUserDetails(user);
    }
}