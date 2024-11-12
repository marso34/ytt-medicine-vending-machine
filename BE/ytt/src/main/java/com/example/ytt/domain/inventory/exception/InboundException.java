package com.example.ytt.domain.inventory.exception;

import com.example.ytt.global.error.exception.BaseException;
import com.example.ytt.global.error.code.ExceptionType;

public class InboundException extends BaseException {
    public InboundException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
