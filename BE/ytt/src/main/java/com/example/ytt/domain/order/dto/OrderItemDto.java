package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상품 Response DTO", description = "주문 상품 정보를 담은 DTO")
public record OrderItemDto(
        @Schema(description = "주문 상품 ID")  Long id,
        @Schema(description = "약 ID")       Long medicineId,
        @Schema(description = "약 이름")      String medicineName,
        @Schema(description = "상품 코드")     String productCode,
        @Schema(description = "이미지")        String imageURL,
        @Schema(description = "수량")         int quantity,
        @Schema(description = "총 가격")       int totalPrice
) {
    public static OrderItemDto of(Long id, Long medicineId, String medicineName, String productCode, String imageURL, int quantity, int totalPrice) {
        return new OrderItemDto(id, medicineId,medicineName, productCode, imageURL, quantity, totalPrice);
    }

    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getMedicine().getId(),
                orderItem.getMedicine().getName(),
                orderItem.getMedicine().getProductCode(),
                orderItem.getMedicine().getImageURL(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice()
        );
    }
}