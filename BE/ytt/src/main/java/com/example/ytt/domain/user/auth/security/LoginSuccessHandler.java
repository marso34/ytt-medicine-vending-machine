package com.example.ytt.domain.user.auth.security;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.domain.RefreshEntity;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.expiration.access}")
    private Long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRATION;


    // [로그인 실행 5] 인증 성공하여 로그인 성공하면 실행하는 핸들러
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 유저 정보
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();

        // 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        // Role을 String에서 enum으로 변환
        Role role = Role.valueOf(auth.getAuthority()); // auth.getAuthority()는 String을 반환하므로 Role로 변환

        // 토큰 생성
        String access = jwtUtil.createJwt("access", email, role, ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt("refresh", email, role, REFRESH_TOKEN_EXPIRATION);

        //Refresh 토큰 저장
        addRefreshEntity(email, refresh, 86400000L);

        // 응답 설정
        response.setHeader("access", access);
        response.addHeader("refresh", refresh);

        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

}
