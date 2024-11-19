package com.example.ytt.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "자판기 주문 상세  DTO", description = "자판기 주문 상세 DTO")
public record OrderDetailVendingMachineDto(
        @Schema(description = "약 번호") String productCode,
        @Schema(description = "수량") int quantity
) {
}
