package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.SignInDto;
import com.example.ytt.domain.user.dto.SignUpDto;
import com.example.ytt.domain.user.dto.UpdatePasswordDto;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.service.UserService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "유저", description = "유저 API")
public class UserController {

    // 로그인 경로("/signIn")
    // 로그아웃 경로("/logout")

    @Autowired
    private final UserService userService;

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
        userService.signUp(user);
        return ResponseEntity.ok(ResponseDto.of(200, "회원가입이 완료되었습니다.", "회원가입 성공"));
    }

    /*
     비밀번호 수정
     */
    @PostMapping("/password")
    @SwaggerApi(summary = "비밀번호 수정", description = "현재 비밀번호를 확인 후 새 비밀번호로 수정합니다.", implementation = String.class)
    @Parameters({
            @Parameter(name = "currentPassword", description = "현재 로그인된 사용자 비밀번호"),
            @Parameter(name = "newPassword", description = "새로 바꿀 비밀번호")
    })
    public ResponseEntity<?> updatePassword(@Validated @RequestBody UpdatePasswordDto updatePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        userService.updatePassword(email, updatePasswordDto.getCurrentPassword(), updatePasswordDto.getNewPassword());
        return ResponseEntity.ok(ResponseDto.of(200, "비밀번호가 성공적으로 수정되었습니다.", "비밀번호 수정 성공"));
    }


    /*
     마이페이지 정보(현재 이메일만 표시)
     */
    @GetMapping("/mypage")
    @SwaggerApi(summary = "마이페이지 정보", description = "현재 로그인한 사용자의 정보를 조회합니다.", implementation = UserDto.class)
    public ResponseEntity<ResponseDto<UserDto>> getCurrentUser() {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(ResponseDto.of(200, "사용자 정보 조회 성공", userDto));
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
}
