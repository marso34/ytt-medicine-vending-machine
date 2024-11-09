package com.example.ytt.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상세 Request DTO", description = "주문 상세 요청 DTO")
public record OrderDetailReqDto(
        @Schema(description = "약 번호") String productCode,
        @Schema(description = "수량") int quantity
) {
}