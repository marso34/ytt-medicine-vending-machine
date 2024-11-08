package com.example.ytt.domain.vendingmachine.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class VendingMachineException extends BaseException {
    public VendingMachineException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
