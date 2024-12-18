package com.example.ytt.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private int status;
    private String message;

    public LoginResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}