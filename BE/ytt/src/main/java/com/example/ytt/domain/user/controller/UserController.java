package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.SignUpDto;
import com.example.ytt.domain.user.dto.TokenResponseDto;
import com.example.ytt.domain.user.service.UserService;
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
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    // TODO: Auth , User 책임분리

    // 로그인 경로("/signIn")
    // 로그아웃 경로("/logout")

    @Autowired
    private final UserService userService;

    /*
     회원가입
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto user) {
        userService.signUp(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }


    /*
    토큰 갱신
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = userService.reissueTokens(request, response);
        return ResponseEntity.ok(tokenResponseDto);
    }

}
