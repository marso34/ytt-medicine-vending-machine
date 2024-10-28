package com.example.ytt.domain.vendingmachine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "자판기 약 등록 임시 요청 DTO", description = "자판기에 약을 등록하기 위한 임시 요청 DTO")
public record TempReqDto(
        @Schema(description = "자판기 ID")   long machine_id,
        @Schema(description = "약 ID")   long medicine_id,
        @Schema(description = "약 수량")  int quantity
) {
}
