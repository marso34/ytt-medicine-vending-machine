package com.example.ytt.domain.user.auth.security;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.domain.JwtRefresh;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.repository.RefreshRepository;
import com.example.ytt.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;


    // [로그인 실행 5] 인증 성공하여 로그인 성공하면 실행하는 핸들러
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 유저 정보
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        // Role을 String에서 enum으로 변환
        Role role = Role.valueOf(auth.getAuthority()); // auth.getAuthority()는 String을 반환하므로 Role로 변환

        // 토큰 생성
        String Authorization = jwtUtil.createAuthorizationToken("Authorization", user.getId(), email, role);
        String refresh = jwtUtil.createRefreshToken("refresh", user.getId(), email, role);

        //Refresh 토큰 저장
        addRefreshEntity(user, refresh);

        // 응답 설정
        response.setHeader("Authorization", Authorization);
        response.addHeader("refresh", refresh);

        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(User user, String refresh) {

        Date date = new Date(System.currentTimeMillis() + jwtUtil.getRefreshTokenExpiration());

        JwtRefresh jwtRefresh = new JwtRefresh();
        jwtRefresh.setUser(user);
        jwtRefresh.setRefresh(refresh);
        jwtRefresh.setExpiration(date);

        refreshRepository.save(jwtRefresh);
    }

}
