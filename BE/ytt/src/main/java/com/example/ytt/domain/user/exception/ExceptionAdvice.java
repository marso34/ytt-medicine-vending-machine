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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // UserExceptionType 예외 핸들링
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionDto> handleBaseEx(BaseException exception) {

        return new ResponseEntity<>(new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getErrorMessage()), // 메시지 포함
                exception.getExceptionType().getHttpStatus());
    }

    // @Valid, @NotBlank 어노테이션에 대한 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.isEmpty() ? "유효성 검증 오류" : fieldErrors.get(0).getDefaultMessage();

        UserExceptionType exceptionType = UserExceptionType.SIGNUP_FORMAT_INVALID; // 기본 형식 오류 타입

        for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("password")) {
                exceptionType = UserExceptionType.PASSWORD_FORMAT_INVALID; // 비밀번호 형식 오류
            } else if (fieldError.getField().equals("name") || fieldError.getField().equals("phoneNumber")) {
                exceptionType = UserExceptionType.NOTBLANK_FORMAT_INVALID; // 공백 필드 오류
            }
        }

        return new ResponseEntity<>(new ExceptionDto(
                exceptionType.getErrorCode(),
                exceptionType.getErrorMessage()),
                HttpStatus.BAD_REQUEST); // 400 Bad Request 반환
    }

    // @Email 제약 조건에 대한 예외 처리 (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(new ExceptionDto(
                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorCode(),
                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorMessage()),
                HttpStatus.BAD_REQUEST); // 400 Bad Request 반환
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadableExceptionEx(HttpMessageNotReadableException exception){

        log.error("Json을 파싱하는 과정에서 예외 발생! {}", exception.getMessage() );
        return new ResponseEntity(new ExceptionDto(3000,"JSON 파싱과정 에러"),HttpStatus.BAD_REQUEST);
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

