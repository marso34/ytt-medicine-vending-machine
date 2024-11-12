package com.example.ytt.domain.user.service;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.domain.JwtRefresh;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.dto.TokenResponseDto;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.exception.UserExceptionType;
import com.example.ytt.domain.user.repository.RefreshRepository;
import com.example.ytt.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // 토큰 재발급 요청 확인
    public TokenResponseDto reissueTokens(HttpServletRequest request, HttpServletResponse response) throws UserException {
        String refresh = request.getHeader("refresh");

        validateRefreshToken(refresh);
        return generateNewTokens(refresh, response);
    }

    // 토큰 재발급
    private TokenResponseDto generateNewTokens(String refresh, HttpServletResponse response) {
        String email = jwtUtil.getEmail(refresh);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        String roleString = jwtUtil.getRole(refresh); // 역할 문자열 가져오기
        Role role = Role.valueOf(roleString.toUpperCase());

        // 새로운 JWT 생성
        String newAuthorization = jwtUtil.createAuthorizationToken("Authorization", user.getId(), email, role);
        String newRefresh = jwtUtil.createRefreshToken("refresh", user.getId(), email, role);

        // 기존 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(user, newRefresh, jwtUtil.getRefreshTokenExpiration());

        // 응답 헤더에 새로운 토큰 설정
        response.setHeader("Authorization", "Bearer " + newAuthorization);
        response.setHeader("refresh", newRefresh);

        return new TokenResponseDto(newAuthorization, newRefresh);
    }

    // [로그인 실행 3] security DB 로그인 인증
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return new CustomUserDetails(userDto);
    }

    // 서버에 저장되는 RefreshEntity 생성 메소드
    private void addRefreshEntity(User user, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        JwtRefresh jwtRefresh = new JwtRefresh();
        jwtRefresh.setUser(user);
        jwtRefresh.setRefresh(refresh);
        jwtRefresh.setExpiration(date);

        refreshRepository.save(jwtRefresh);
    }

    public void removeUserRefreshTokens(User user) {
        refreshRepository.deleteByUser(user);
    }

    // 리프레시 토큰 유효성 검사
    private void validateRefreshToken(String refresh) throws UserException {
        if (refresh == null || refresh.trim().isEmpty()) {
            throw new UserException(UserExceptionType.BLANK_REFRESH_TOKEN);
        }

        try {
            if (jwtUtil.isExpired(refresh)) {
                throw new UserException(UserExceptionType.EXPIRED_REFRESH_TOKEN);
            }
        }catch (Exception e) {
            throw new UserException(UserExceptionType.INVALID_REFRESH_TOKEN);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new UserException(UserExceptionType.INVALID_TOKEN_CATEGORY);
        }

        if (!refreshRepository.existsByRefresh(refresh)) {
            throw new UserException(UserExceptionType.INVALID_REFRESH_TOKEN);
        }
    }
}
