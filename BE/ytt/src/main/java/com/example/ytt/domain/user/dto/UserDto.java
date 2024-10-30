package com.example.ytt.domain.user.dto;

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

}

