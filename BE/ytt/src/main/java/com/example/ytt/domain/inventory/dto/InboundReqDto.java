package com.example.ytt.domain.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "입고 요청 DTO", description = "입고 요청 정보를 담은 DTO")
public record InboundReqDto(
        @Schema(description = "약 ID")    long medicineId,
        @Schema(description = "입고량")    int quantity
) {

}
