package com.example.ytt.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    // TODO: 에러 코드 정리하기

    /*

    클라이언트에 명확한 에러 전달 하기 위함

    이메일/비밀번호 형식 오류는 HttpStatus.BAD_REQUEST (400).
            **이미 존재하는 리소스(이메일/전화번호)**는 HttpStatus.CONFLICT (409).
    잘못된 비밀번호는 HttpStatus.UNAUTHORIZED (401).
    사용자 정보를 찾을 수 없는 경우는 HttpStatus.NOT_FOUND (404).

    */

    // 권한이 없는 유저
    FORBIDDEN_USER(403, HttpStatus.FORBIDDEN, "권한이 없습니다."), // 403 Forbidden

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
    INVALID_REFRESH_TOKEN(622, HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."), // 400 Bad Request

    // 자판기 에러
    NOT_FOUND_VENDING_MACHINE(700, HttpStatus.NOT_FOUND, "자판기 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_VENDING_MACHINE(701, HttpStatus.NO_CONTENT, "자판기 정보가 없습니다."), // 204 No Content

    ALREADY_EXIST_LOCATION(710, HttpStatus.CONFLICT, "이미 존재하는 위치입니다."), // 409 Conflict, 아직 사용X

    // 약 에러
    NOT_FOUND_MEDICINE(800, HttpStatus.NOT_FOUND, "약 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_MEDICINE(801, HttpStatus.NO_CONTENT, "조건에 맞는 약 정보가 없습니다."), // 204 No Content
    BAD_REQUEST_MEDICINE_SEARCH(802, HttpStatus.BAD_REQUEST, "약 ID 또는 약품 코드를 입력해주세요"), // 400 Bad Request

    ALREADY_EXIST_MEDICINE(810, HttpStatus.CONFLICT, "이미 등록된 약입니다."), // 409 Conflict
    NOT_FOUND_MEDICINE_REGISTER(811, HttpStatus.NOT_FOUND, "약 등록 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_MEDICINE_REGISTER(812, HttpStatus.NO_CONTENT, "약 등록 정보가 없습니다."), // 204 No Content
    ALREADY_EXIST_MEDICINE_REGISTER(813, HttpStatus.CONFLICT, "이미 등록된 약입니다."), // 409 Conflict

    // 약 성분 에러
    NOT_FOUND_INGREDIENT(900, HttpStatus.NOT_FOUND, "약 성분 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_INGREDIENT(901, HttpStatus.NO_CONTENT, "약 성분을 가진 약이 없습니다."), // 204 No Content
//    ALREADY_EXIST_INGREDIENT(902, HttpStatus.CONFLICT, "이미 존재하는 약 성분입니다."), // 409 Conflict

    // 입고 에러
    NOT_FOUND_INBOUND_LOG(1000, HttpStatus.NOT_FOUND, "입고 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_INBOUND_LOG(1001, HttpStatus.NO_CONTENT, "입고 정보가 없습니다."), // 204 No Content
    UNREGISTERED_INBOUND(1002, HttpStatus.BAD_REQUEST, "등록되지 않은 약의 입고는 불가합니다."), // 400 Bad Request

    NOT_FOUND_INVENOTRY(1010, HttpStatus.NOT_FOUND, "재고 정보가 없습니다."), // 404 Not Found
    NO_CONTENT_INVENOTRY(1011, HttpStatus.NO_CONTENT, "재고 정보가 없습니다."), // 204 No Content
    
    // 주문
    NOT_FOUND_ORDER(1100, HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATE_PENDING(1102, HttpStatus.BAD_REQUEST, "대기 중인 주문만 처리할 수 있습니다.."),
    INVALID_ORDER_STATE_STORED(1103, HttpStatus.BAD_REQUEST, "보관 중인 주문만 처리할 수 있습니다."),
    INSUFFICIENT_INVENTORY(1104, HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    ORDER_STORE_SUCCESS(200, HttpStatus.OK, "보관 요청이 성공적으로 처리되었습니다."),
    ORDER_COMPLETE_SUCCESS(200, HttpStatus.OK, "수령 완료 처리가 성공적으로 처리되었습니다."),
    ORDER_CANCEL_SUCCESS(200, HttpStatus.OK, "주문 취소가 성공적으로 처리되었습니다."),
    WEBSOCKET_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),

    // Json 형식 오류
    JSON_FORMAT_INVALID(3000, HttpStatus.BAD_REQUEST, "JSON 형식이 올바르지 않습니다."), // 400 Bad Request

    // 서버 에러
    SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."); // 500 Internal Server Error

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}