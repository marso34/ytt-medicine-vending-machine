package com.example.ytt.global.error;

import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    public static final String DEFAULT_ERROR_MESSAGE = "ERROR : {}";

    // @Valid, @NotBlank 어노테이션에 대한 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        ExceptionType exceptionType = ExceptionType.SIGNUP_FORMAT_INVALID; // 형식 오류 default

        for (FieldError fieldError : fieldErrors) {
            // @NotBlank 에러 처리
            if (fieldError.getCode().equals("NotBlank")) {
                exceptionType = ExceptionType.NOTBLANK_FORMAT_INVALID; // 공백 필드 오류
                break;
            }
            // 비밀번호 형식 에러 처리
            else if (fieldError.getField().equals("password") || fieldError.getField().equals("newPassword")) {
                exceptionType = ExceptionType.PASSWORD_FORMAT_INVALID; // 비밀번호 형식 오류
            }
            // 핸드폰 번호 형식 에러
            else if (fieldError.getField().equals("phoneNumber")) {
                exceptionType = ExceptionType.PHONENUMBER_FORMAT_INVALID; // 폰번호 형식 오류
            }
        }

//        log.warn(DEFAULT_ERROR_MESSAGE, exceptionType.getErrorMessage(), exceptionType);
        return ResponseUtil.error(exceptionType, null);
    }

    // @Email 제약 조건에 대한 예외 처리 (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(DEFAULT_ERROR_MESSAGE, ex.getMessage(), ex);
        return ResponseUtil.error(ExceptionType.EMAIL_FORMAT_INVALID, null);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto<Object>> handleBaseEx(BaseException ex) {
        log.warn(DEFAULT_ERROR_MESSAGE, ex.getMessage(), ex);
        return ResponseUtil.error(ex.getExceptionType(), null);
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Object>> httpMessageNotReadableExceptionEx(HttpMessageNotReadableException ex){
        log.error(DEFAULT_ERROR_MESSAGE, ex.getMessage() );
        return ResponseUtil.error(ExceptionType.JSON_FORMAT_INVALID, null);
    }

    // 기타 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleMemberEx(Exception ex) {
        log.error(DEFAULT_ERROR_MESSAGE, ex.getMessage(), ex);
        return ResponseUtil.error(ExceptionType.SERVER_ERROR, null);
    }

}