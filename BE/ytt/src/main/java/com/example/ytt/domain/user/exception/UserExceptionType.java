package com.example.ytt.domain.user.exception;

import com.example.ytt.global.error.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum UserExceptionType implements BaseExceptionType {

    /*
    
    클라이언트에 명확한 에러 전달 하기 위함
     
    이메일/비밀번호 형식 오류는 HttpStatus.BAD_REQUEST (400).
            **이미 존재하는 리소스(이메일/전화번호)**는 HttpStatus.CONFLICT (409).
    잘못된 비밀번호는 HttpStatus.UNAUTHORIZED (401).
    사용자 정보를 찾을 수 없는 경우는 HttpStatus.NOT_FOUND (404).
    
    */

    ALREADY_EXIST_USER(600, HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."), // 409 Conflict
    ALREADY_EXIST_PHONENUMBER(601, HttpStatus.CONFLICT, "이미 가입된 번호입니다."), // 409 Conflict
    WRONG_PASSWORD(602, HttpStatus.UNAUTHORIZED, "비밀번호가 잘못되었습니다."), // 401 Unauthorized
    NOT_FOUND_USER(603, HttpStatus.NOT_FOUND, "회원 정보가 없습니다."), // 404 Not Found
    SIGNUP_FORMAT_INVALID(604, HttpStatus.BAD_REQUEST, "유효성 에러."), // 400 Bad Request
    EMAIL_FORMAT_INVALID(605, HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."), // 400 Bad Request
    PASSWORD_FORMAT_INVALID(606, HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."), // 400 Bad Request
    NOTBLANK_FORMAT_INVALID(607, HttpStatus.BAD_REQUEST, "공백이 존재할 수 없습니다."); // 400 Bad Request

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    UserExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage){
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
