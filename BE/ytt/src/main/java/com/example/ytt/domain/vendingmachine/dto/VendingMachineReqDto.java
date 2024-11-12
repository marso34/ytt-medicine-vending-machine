package com.example.ytt.domain.vendingmachine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "자판기 생성 Request DTO", description = "자판기 생성 요청 DTO")
public record VendingMachineReqDto(
        @Schema(description = "자판기 이름")     String name,
        @Schema(description = "자판기 주소")     String location,
        @Schema(description = "자판기 위도")     double latitude,
        @Schema(description = "자판기 경도")     double longitude,
        @Schema(description = "자판기 용량")     int quantity
) {
}
