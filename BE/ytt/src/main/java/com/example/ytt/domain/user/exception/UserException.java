package com.example.ytt.domain.user.exception;

import com.example.ytt.global.error.exception.BaseException;
import com.example.ytt.global.error.code.ExceptionType;

public class UserException extends BaseException {

    public UserException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}