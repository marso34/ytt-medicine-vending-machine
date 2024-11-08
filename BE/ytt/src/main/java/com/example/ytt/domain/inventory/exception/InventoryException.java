package com.example.ytt.domain.inventory.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class InventoryException extends BaseException {

    public InventoryException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}
