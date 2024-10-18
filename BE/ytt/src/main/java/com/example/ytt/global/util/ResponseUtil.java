package com.example.ytt.global.util;


import com.example.ytt.global.common.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * ResponseDto 유틸리티 클래스
 */
public class ResponseUtil {

    // private 생성자: 인스턴스화를 방지
    private ResponseUtil() {
        throw new UnsupportedOperationException("Utility Class");
    }

    private static final String DEFAULT_SUCCESS_MESSAGE = "성공적으로 처리 완료";
    private static final HttpStatus DEFAULT_SUCCESS_STATUS = HttpStatus.OK;

    public static <T> ResponseEntity<ResponseDto<T>> success(T body) {
        return success(DEFAULT_SUCCESS_STATUS, DEFAULT_SUCCESS_MESSAGE, body);
    }

    public static <T> ResponseEntity<ResponseDto<T>> success(String message, T body) {
        return success(DEFAULT_SUCCESS_STATUS, message, body);
    }

    public static <T> ResponseEntity<ResponseDto<T>> success(HttpStatus status, String message, T body) {
        return ResponseEntity
                .status(status)
                .body(ResponseDto.of(status.value(), message, body));
    }

}
