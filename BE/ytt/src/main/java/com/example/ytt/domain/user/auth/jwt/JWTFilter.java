package com.example.ytt.domain.user.auth.jwt;

import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.dto.UserDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

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

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(authorizationToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("AuthorizationToken expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 Authorization인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(authorizationToken);

        if (!category.equals("Authorization")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid Authorization token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        String email = jwtUtil.getEmail(authorizationToken);
        String roleString = jwtUtil.getRole(authorizationToken); // 역할 문자열 가져오기
        // Role 객체로 변환
        Role role = Role.valueOf(roleString.toUpperCase());

        UserDto user = new UserDto();
        user.setEmail(email);
        user.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
