package com.example.ytt.domain.user.dto;

import com.example.ytt.domain.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userid;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private String phoneNumber;
    private Role role;

    public static UserDto from(User user) {
        return UserDto.builder()
                .userid(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }

}

