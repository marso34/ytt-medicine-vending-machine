package com.example.ytt.global.util;

import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

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

    /**
     * 임시 에러 응답, ExceptionType 방식으로 수정
     */
    public static <T> ResponseEntity<ResponseDto<T>> error(HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(ResponseDto.of(status.value(), status.getReasonPhrase(), null));
    }

    /**
     * 임시 에러 응답, ExceptionType 방식으로 수정
     */
    public static <T> ResponseEntity<ResponseDto<T>> error(int code, String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(ResponseDto.of(code, message, null));
    }

    public static <T> ResponseEntity<ResponseDto<T>> error(ExceptionType status, T body) {
        return error(status, status.getErrorMessage(), body);
    }

    public static <T> ResponseEntity<ResponseDto<T>> error(ExceptionType status, String message, T body) {
        return ResponseEntity
                .status(status.getHttpStatus())
                .body(ResponseDto.of(status.getErrorCode(), message, body));
    }

    /**
     * 성공 응답을 HttpServletResponse에 전송
     */
    public static void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        ResponseDto<String> responseDto = ResponseDto.of(HttpStatus.OK.value(), DEFAULT_SUCCESS_MESSAGE, message);
        String jsonResponse = new ObjectMapper().writeValueAsString(responseDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 에러 응답을 HttpServletResponse에 전송
     */
    public static void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        ResponseDto<String> responseDto = ResponseDto.of(code, message, null);
        String jsonResponse = new ObjectMapper().writeValueAsString(responseDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setStatus(code);
    }

    public static void sendErrorResponse(HttpServletResponse response, ExceptionType exceptionType) throws IOException {
        ResponseDto<String> responseDto = ResponseDto.of(exceptionType.getHttpStatus().value(), exceptionType.getErrorMessage(), null);
        String jsonResponse = new ObjectMapper().writeValueAsString(responseDto);

        response.setStatus(exceptionType.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

}
