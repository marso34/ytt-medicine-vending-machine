package com.example.ytt.domain.order.exception;

import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.error.exception.BaseException;

public class OrderException extends BaseException {
    public OrderException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
