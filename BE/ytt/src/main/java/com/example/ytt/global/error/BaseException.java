package com.example.ytt.global.error;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final ExceptionType exceptionType;

    protected BaseException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }
}