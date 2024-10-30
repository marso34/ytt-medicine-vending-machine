package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.TokenResponseDto;
import com.example.ytt.domain.user.service.AuthService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private final AuthService authService;

    /*
    토큰 갱신
     */
    @PostMapping("/reissue")
    @SwaggerApi(summary = "토큰 갱신", description = "사용자의 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.", implementation = TokenResponseDto.class)
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.reissueTokens(request, response);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
