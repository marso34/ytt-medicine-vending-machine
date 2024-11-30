package com.example.ytt.domain.order.dto.request;

import com.example.ytt.domain.order.domain.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상세 DTO", description = "주문 상세 DTO")
public record OrderItemReqDto(
        @Schema(description = "약 번호") String productCode,
        @Schema(description = "수량")   int quantity
) {
    public static OrderItemReqDto of(String productCode, int quantity) {
        return new OrderItemReqDto(productCode, quantity);
    }

    public static OrderItemReqDto from(OrderItem orderItem) {
        return new OrderItemReqDto(orderItem.getMedicine().getProductCode(), orderItem.getQuantity());
    }

}