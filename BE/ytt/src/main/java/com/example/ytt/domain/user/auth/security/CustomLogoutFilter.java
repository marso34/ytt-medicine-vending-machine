package com.example.ytt.domain.user.auth.security;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.exception.UserExceptionType;
import com.example.ytt.domain.user.repository.RefreshRepository;
import com.example.ytt.global.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/user\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            ResponseUtil.sendErrorResponse(response, HttpStatus.METHOD_NOT_ALLOWED.value(), "허용되지 않은 메서드입니다.");
            return;
        }

        //get refresh token
        String refresh = request.getHeader("refresh");

        //refresh null check
        if (refresh == null) {
            ResponseUtil.sendErrorResponse(response,UserExceptionType.BLANK_REFRESH_TOKEN);
            return;
        }

        //expired check
        try {
            if(jwtUtil.isExpired(refresh)){
                ResponseUtil.sendErrorResponse(response,UserExceptionType.EXPIRED_REFRESH_TOKEN);
                return;
            }
        } catch (Exception e) {
            ResponseUtil.sendErrorResponse(response, UserExceptionType.INVALID_REFRESH_TOKEN);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            ResponseUtil.sendErrorResponse(response,UserExceptionType.INVALID_REFRESH_TOKEN);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            ResponseUtil.sendErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "해당 Refresh 토큰이 존재하지 않습니다.");
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);

        ResponseUtil.sendSuccessResponse(response, "로그아웃이 완료되었습니다.");
    }
}
