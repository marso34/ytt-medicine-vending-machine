package com.example.ytt.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePasswordDto {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{12,20}", message = "비밀번호는 12~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String newPassword;
}
