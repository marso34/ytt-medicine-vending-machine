package com.example.ytt.domain.medicine.exception;

import com.example.ytt.global.error.exception.BaseException;
import com.example.ytt.global.error.code.ExceptionType;

public class MedicineException extends BaseException {
    public MedicineException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
