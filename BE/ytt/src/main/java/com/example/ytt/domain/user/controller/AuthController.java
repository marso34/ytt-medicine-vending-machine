package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.TokenResponseDto;
import com.example.ytt.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {


    @Autowired
    private final AuthService authService;

    /*
    토큰 갱신
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.reissueTokens(request, response);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
