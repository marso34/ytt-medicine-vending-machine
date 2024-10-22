package com.example.ytt.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "response DTO", description = "응답 DTO")
public record ResponseDto<T>(
        @Schema(description = "code")     int code,
        @Schema(description = "message")  String message,
        @Schema(description = "body")     T body
) {

        public static <T> ResponseDto<T> of(int code, String message, T body) {
            return new ResponseDto<>(code, message, body);
        }
}
