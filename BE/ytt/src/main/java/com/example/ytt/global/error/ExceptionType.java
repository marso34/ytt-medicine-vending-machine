package com.example.ytt.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

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
    PHONENUMBER_FORMAT_INVALID(607, HttpStatus.BAD_REQUEST, "핸드폰번호 형식이 올바르지 않습니다."), // 400 Bad Request
    NOTBLANK_FORMAT_INVALID(608, HttpStatus.BAD_REQUEST, "공백이 존재할 수 없습니다."), // 400 Bad Request

    INVALID_TOKEN_CATEGORY(610, HttpStatus.BAD_REQUEST, "토큰 타입이 올바르지 않습니다."), // 400 Bad Request
    BLANK_ACCESS_TOKEN(610, HttpStatus.UNAUTHORIZED, "액세스 토큰이 비었습니다."), // 401 Unauthorized
    EXPIRED_ACCESS_TOKEN(611, HttpStatus.BAD_REQUEST, "액세스 토큰이 만료되었습니다."), // 400 Bad Request
    INVALID_ACCESS_TOKEN(612, HttpStatus.BAD_REQUEST, "유효하지 않은 액세스 토큰입니다."), // 400 Bad Request

    BLANK_REFRESH_TOKEN(620, HttpStatus.BAD_REQUEST, "리프레시 토큰이 비었습니다."), // 400 Bad Request
    EXPIRED_REFRESH_TOKEN(621, HttpStatus.BAD_REQUEST, "리프레시 토큰이 만료되었습니다."), // 400 Bad Request
    INVALID_REFRESH_TOKEN(622, HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."); // 400 Bad Request



    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}