package com.example.ytt.global.error;

public abstract class BaseException extends RuntimeException{

    protected BaseException(String message){
        super(message);
    }

    public abstract BaseExceptionType getExceptionType();
}