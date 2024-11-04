package com.example.ytt.domain.user.auth.jwt;

import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.exception.UserExceptionType;
import com.example.ytt.global.common.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 Authorization키에 담긴 토큰을 꺼냄
        String authorizationToken = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘김
        if (authorizationToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationToken.startsWith("Bearer ")) {
            sendErrorResponse(response, UserExceptionType.INVALID_ACCESS_TOKEN);
            return;
        }

        // Bearer 접두사 제거
        authorizationToken = authorizationToken.substring(7); // "Bearer " 부분 제거

        // 토큰 검증 및 만료 여부 확인
        try {
            if (jwtUtil.isExpired(authorizationToken)) {
                sendErrorResponse(response, UserExceptionType.EXPIRED_ACCESS_TOKEN);
                return;
            }
        } catch (Exception e) {
            sendErrorResponse(response, UserExceptionType.INVALID_ACCESS_TOKEN);
            return;
        }

        // 토큰이 Authorization인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(authorizationToken);

        if (!category.equals("Authorization")) {
            sendErrorResponse(response, UserExceptionType.INVALID_TOKEN_CATEGORY);
            return;
        }

        // 사용자 정보 가져오기
        Long userId = jwtUtil.getUserId(authorizationToken);
        String email = jwtUtil.getEmail(authorizationToken);
        String roleString = jwtUtil.getRole(authorizationToken);
        Role role = Role.valueOf(roleString.toUpperCase());

        UserDto user = new UserDto();
        user.setUserid(userId);
        user.setEmail(email);
        user.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
    private void sendErrorResponse(HttpServletResponse response, UserExceptionType exceptionType) throws IOException {
        ResponseDto<String> responseDto = ResponseDto.of(exceptionType.getHttpStatus().value(), exceptionType.getErrorMessage(), null);
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.setStatus(exceptionType.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
