package com.example.ytt.global.error.code;

import org.springframework.http.HttpStatus;

public record ExceptionCode(
        int errorCode,
        HttpStatus httpStatus,
        String errorMessage
) {
    public static ExceptionCode from(ExceptionType ex) {
        return new ExceptionCode(ex.getErrorCode(), ex.getHttpStatus(), ex.getErrorMessage());
    }

    public int getErrorType() {
        return errorCode / 100;
    }
}
