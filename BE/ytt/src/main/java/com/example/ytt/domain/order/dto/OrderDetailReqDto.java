package com.example.ytt.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상세 DTO", description = "주문 상세 DTO")
public record OrderDetailReqDto(
        @Schema(description = "약 번호") String productCode,
        @Schema(description = "수량") int quantity
) {
    public static OrderDetailReqDto of(String productCode, int quantity) {
        return new OrderDetailReqDto(productCode, quantity);
    }

}