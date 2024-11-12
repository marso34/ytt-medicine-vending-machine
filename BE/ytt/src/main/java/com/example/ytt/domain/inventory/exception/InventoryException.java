package com.example.ytt.domain.inventory.exception;

import com.example.ytt.global.error.exception.BaseException;
import com.example.ytt.global.error.code.ExceptionType;

public class InventoryException extends BaseException {

    public InventoryException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}
