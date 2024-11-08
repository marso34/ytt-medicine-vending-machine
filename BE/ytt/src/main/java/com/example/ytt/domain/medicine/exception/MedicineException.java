package com.example.ytt.domain.medicine.exception;

import com.example.ytt.global.error.BaseException;
import com.example.ytt.global.error.ExceptionType;

public class MedicineException extends BaseException {
    public MedicineException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
