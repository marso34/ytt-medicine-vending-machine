package com.example.ytt.domain.user.exception;

import com.example.ytt.global.error.BaseException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    // UserExceptionType 예외 핸들링
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionDto> handleUserException(UserException exception) {
        return new ResponseEntity<>(new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getErrorMessage()),
                exception.getExceptionType().getHttpStatus());
    }

    // @Valid, @NotBlank 어노테이션에 대한 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        UserExceptionType exceptionType = UserExceptionType.SIGNUP_FORMAT_INVALID; // 형식 오류 default

        for (FieldError fieldError : fieldErrors) {
            // @NotBlank 에러 처리
            if (fieldError.getCode().equals("NotBlank")) {
                exceptionType = UserExceptionType.NOTBLANK_FORMAT_INVALID; // 공백 필드 오류
                break;
            }
            // 비밀번호 형식 에러 처리
            else if (fieldError.getField().equals("password")) {
                exceptionType = UserExceptionType.PASSWORD_FORMAT_INVALID; // 비밀번호 형식 오류
            }
        }

        return new ResponseEntity<>(new ExceptionDto(
                exceptionType.getErrorCode(),
                exceptionType.getErrorMessage()),
                exceptionType.getHttpStatus());
    }

    // @Email 제약 조건에 대한 예외 처리 (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(new ExceptionDto(
                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorCode(),
                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorMessage()),
                UserExceptionType.EMAIL_FORMAT_INVALID.getHttpStatus());
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> httpMessageNotReadableExceptionEx(HttpMessageNotReadableException exception){
        return new ResponseEntity<>(new ExceptionDto(
                UserExceptionType.JSON_PARSE_ERROR.getErrorCode(),
                UserExceptionType.JSON_PARSE_ERROR.getErrorMessage()),
                UserExceptionType.JSON_PARSE_ERROR.getHttpStatus());
    }

    // 기타 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception) {

        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
        private String errorMessage;
    }
}

