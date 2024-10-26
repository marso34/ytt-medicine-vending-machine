package com.example.ytt.domain.medicine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "약품 요청 DTO", description = "약품 저장 요청 DTO")
public record MedicineRequestDto(
        @Schema(description = "이름")     String productName,
        @Schema(description = "가격")     int productPrice
) {
}
