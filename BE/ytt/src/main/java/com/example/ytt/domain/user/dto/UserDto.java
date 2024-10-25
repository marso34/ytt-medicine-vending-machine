package com.example.ytt.domain.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Role role;

}

