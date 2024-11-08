package com.example.ytt.domain.inventory.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class InboundException extends BaseException {
    public InboundException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
