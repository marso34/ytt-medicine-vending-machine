package com.example.ytt.global.error.util;

import com.example.ytt.global.error.code.ExceptionCode;
import com.example.ytt.global.error.code.ExceptionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ErrorDocs를 위한 ExceptionCode 유틸리티 클래스
 */
public class ErrorCodeUtil {

    private ErrorCodeUtil() {
        throw new UnsupportedOperationException("Utility Class");
    }

    public static List<ExceptionCode> getErrors() {
        return Arrays.stream(ExceptionType.values())
                .map(ExceptionCode::from)
                .toList();
    }

    public static Map<Integer, List<ExceptionCode>> getGroupedErrors() {
        return Arrays.stream(ExceptionType.values())
                .map(ExceptionCode::from)
                .sorted((e1, e2) -> Integer.compare(e1.errorCode(), e2.errorCode()))
                .collect(Collectors.groupingBy(ExceptionCode::getErrorType));
    }
}
