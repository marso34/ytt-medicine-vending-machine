package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.SignUpDto;
import com.example.ytt.domain.user.dto.UpdatePasswordDto;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
     비밀번호 수정
     */
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody UpdatePasswordDto updatePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updatePassword(email, updatePasswordDto.getCurrentPassword(), updatePasswordDto.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 수정되었습니다.");
    }

    /*
     마이페이지 정보(현재 이메일만 표시)
     */
    @GetMapping("/mypage")
    public ResponseEntity<UserDto> getCurrentUser() {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }




}
