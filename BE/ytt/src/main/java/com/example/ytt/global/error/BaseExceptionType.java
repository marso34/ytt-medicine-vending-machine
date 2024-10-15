package com.example.ytt.global.error;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    int getErrorCode(); // 에러코드

    HttpStatus getHttpStatus(); // Http 상태

    String getErrorMessage(); // 에러메세지
}
