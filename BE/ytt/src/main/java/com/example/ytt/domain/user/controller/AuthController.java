package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.SignInDto;
import com.example.ytt.domain.user.dto.SignUpDto;
import com.example.ytt.domain.user.dto.TokenResponseDto;
import com.example.ytt.domain.user.service.AuthService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증", description = "인증 API")
public class AuthController {

    @Autowired
    private final AuthService authService;

    /*
     회원가입
     */
    @PostMapping("/signUp")
    @SwaggerApi(summary = "회원가입", description = "새로운 사용자를 등록합니다.", implementation = String.class)
    @Parameters({
            @Parameter(name = "email", description = "이메일", example = "example@naver.com"),
            @Parameter(name = "password", description = "비밀번호는 12~20자 영문 대 소문자, 숫자, 특수문자를 사용", example = "!!Example123456"),
            @Parameter(name = "name", description = "이름", example = "홍길동"),
            @Parameter(name = "phoneNumber", description = "핸드폰번호는 xxx-xxxx-xxxx 양식", example = "010-1234-5678"),
    })
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto user) {
        authService.signUp(user);
        return ResponseEntity.ok(ResponseDto.of(200, "회원가입이 완료되었습니다.", "회원가입 성공"));
    }

    /*
     로그인
     */
    @PostMapping("/signIn")
    @SwaggerApi(summary = "로그인", description = "이메일과 비밀번호로 사용자를 인증하고 JWT 토큰을 헤더에 발급합니다.", implementation = String.class)
    @Parameters({
            @Parameter(name = "email", description = "이메일", example = "example@naver.com"),
            @Parameter(name = "password", description = "비밀번호", example = "!!Example123456"),
    })
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInDto signInDto) {
        return ResponseEntity.ok(ResponseDto.of(200, "로그인 성공", "토큰이 발급되었습니다."));
    }

    /*
     로그아웃
     */
    @PostMapping("/logout")
    @SwaggerApi(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다.", implementation = String.class)
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(ResponseDto.of(200, "로그아웃이 완료되었습니다.", "로그아웃 성공"));
    }

    /*
    토큰 갱신
     */
    @PostMapping("/reissue")
    @SwaggerApi(summary = "토큰 갱신", description = "사용자의 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.", implementation = TokenResponseDto.class)
    @Parameters({
            @Parameter(name = "refresh", description = "리프레시 토큰을 헤더에 포함하여 전송합니다.")
    })
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.reissueTokens(request, response);
        return ResponseEntity.ok(ResponseDto.of(200, "토큰이 재발급 되었습니다.", tokenResponseDto));
    }
}
