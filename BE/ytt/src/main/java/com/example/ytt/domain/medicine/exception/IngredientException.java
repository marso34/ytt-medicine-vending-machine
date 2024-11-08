package com.example.ytt.domain.medicine.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class IngredientException extends BaseException {

    public IngredientException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
