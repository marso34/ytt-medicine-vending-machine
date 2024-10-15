package com.example.ytt.global.error;

public abstract class BaseException extends RuntimeException{
    public abstract BaseExceptionType getExceptionType();
}
