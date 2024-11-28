package com.example.ytt.domain.user.controller;

import com.example.ytt.domain.user.dto.UpdatePasswordDto;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.service.UserService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Autowired
    private final UserService userService;

    /*
     마이페이지 정보
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
}
