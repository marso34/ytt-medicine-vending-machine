package com.example.ytt.global.error;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final ExceptionType exceptionType;

    protected BaseException(ExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());
        this.exceptionType = exceptionType;
    }
}