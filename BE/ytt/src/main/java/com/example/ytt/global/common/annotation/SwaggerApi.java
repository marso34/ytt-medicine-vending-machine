package com.example.ytt.global.common.annotation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Default Summary",
        description = "Default Description"
)
@ApiResponse(responseCode = "200", description = "성공")
public @interface SwaggerApi {
    String summary() default "Default Summary";
    String description() default "Default Description";
    Class<?> implementation();
}
