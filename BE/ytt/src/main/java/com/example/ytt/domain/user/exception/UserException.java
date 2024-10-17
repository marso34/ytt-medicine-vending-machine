package com.example.ytt.domain.user.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.BaseExceptionType;

public class UserException extends BaseException {
    private BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}