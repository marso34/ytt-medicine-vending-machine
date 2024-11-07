package com.example.ytt.domain.user.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class UserException extends BaseException {

    public UserException(ExceptionType exceptionType) {
        super(exceptionType.getErrorMessage(), exceptionType);
    }

}