package com.example.ytt.domain.vendingmachine.exception;

import com.example.ytt.global.error.exception.BaseException;
import com.example.ytt.global.error.code.ExceptionType;

public class VendingMachineException extends BaseException {
    public VendingMachineException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
