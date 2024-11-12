package com.example.ytt.global.error.exception;

import com.example.ytt.global.error.code.ExceptionType;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final ExceptionType exceptionType;

    protected BaseException(ExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());
        this.exceptionType = exceptionType;
    }
}