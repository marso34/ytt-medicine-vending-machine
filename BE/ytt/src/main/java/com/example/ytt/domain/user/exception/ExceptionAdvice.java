package com.example.ytt.domain.user.exception;

import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.util.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto<Object>> handleBaseEx(BaseException exception) {

        return ResponseUtil.error(exception.getExceptionType(), null);
//        return new ResponseEntity<>(new ExceptionDto(
//                exception.getExceptionType().getErrorCode(),
//                exception.getExceptionType().getErrorMessage()), // 메시지 포함
//                exception.getExceptionType().getHttpStatus());
    }

    // @Valid, @NotBlank 어노테이션에 대한 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        UserExceptionType exceptionType = UserExceptionType.SIGNUP_FORMAT_INVALID; // 형식 오류 default

        for (FieldError fieldError : fieldErrors) {
            // @NotBlank 에러 처리
            if (fieldError.getCode().equals("NotBlank")) {
                exceptionType = UserExceptionType.NOTBLANK_FORMAT_INVALID; // 공백 필드 오류
                break;
            }
            // 비밀번호 형식 에러 처리
            else if (fieldError.getField().equals("password") || fieldError.getField().equals("newPassword")) {
                exceptionType = UserExceptionType.PASSWORD_FORMAT_INVALID; // 비밀번호 형식 오류
            }
            // 핸드폰 번호 형식 에러
            else if (fieldError.getField().equals("phoneNumber")) {
                exceptionType = UserExceptionType.PHONENUMBER_FORMAT_INVALID; // 폰번호 형식 오류
            }
        }


        return ResponseUtil.error(exceptionType, null);
//        return new ResponseEntity<>(new ExceptionDto(
//                exceptionType.getErrorCode(),
//                exceptionType.getErrorMessage()),
//                HttpStatus.BAD_REQUEST); // 400 Bad Request 반환
    }

    // @Email 제약 조건에 대한 예외 처리 (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseUtil.error(UserExceptionType.EMAIL_FORMAT_INVALID, null);
//        return new ResponseEntity<>(new ExceptionDto(
//                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorCode(),
//                UserExceptionType.EMAIL_FORMAT_INVALID.getErrorMessage()),
//                HttpStatus.BAD_REQUEST); // 400 Bad Request 반환
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Object>> httpMessageNotReadableExceptionEx(HttpMessageNotReadableException exception){

        log.error("Json을 파싱하는 과정에서 예외 발생! {}", exception.getMessage() );
        return ResponseUtil.error(3000, "JSON 파싱과정 에러", HttpStatus.BAD_REQUEST);
    }

    // 기타 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleMemberEx(Exception exception) {

        exception.printStackTrace();
        return ResponseUtil.error(HttpStatus.BAD_REQUEST);
    }

}