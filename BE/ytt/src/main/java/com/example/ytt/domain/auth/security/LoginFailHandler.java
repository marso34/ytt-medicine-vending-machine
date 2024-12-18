package com.example.ytt.domain.auth.security;

import com.example.ytt.global.common.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoginFailHandler implements AuthenticationFailureHandler {
    // [로그인 실행 5] 로그인 실패하면 실행하는 핸들러
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        ResponseDto<String> responseDto = ResponseDto.of(HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패", exception.getMessage());
        String jsonResponse = new ObjectMapper().writeValueAsString(responseDto);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(jsonResponse);
    }
}
