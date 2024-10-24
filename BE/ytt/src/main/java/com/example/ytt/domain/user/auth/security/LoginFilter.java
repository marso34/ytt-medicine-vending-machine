package com.example.ytt.domain.user.auth.security;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.dto.SignInDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    // [로그인 실행 1] API 데이터 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        SignInDto signInDto = new SignInDto();

        // JSON 형식의 요청에서 이메일과 비밀번호 추출
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            signInDto = objectMapper.readValue(messageBody, SignInDto.class);

        } catch (IOException e) {
            throw new AuthenticationServiceException("잘못된 key, name으로 요청했습니다.", e);
        }

        String username = signInDto.getEmail();
        String password = signInDto.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return super.getAuthenticationManager().authenticate(authToken);
    }

    // [로그인 실행 4] security 인증 성공 시 실행하는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("Security Login >> 인증 성공");

        // 로그인 성공 핸들러 호출
        AuthenticationSuccessHandler handler = this.getSuccessHandler();
        handler.onAuthenticationSuccess(request, response, authentication);
    }

    // [로그인 실행 4] security 인증 실패 시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("Security Login >> 인증 실패");

        // 로그인 실패 핸들러 호출
        AuthenticationFailureHandler handler = this.getFailureHandler();
        handler.onAuthenticationFailure(request, response, failed);
    }
}