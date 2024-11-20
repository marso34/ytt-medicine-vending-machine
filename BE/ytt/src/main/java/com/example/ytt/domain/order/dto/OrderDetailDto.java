package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상세 Response DTO", description = "주문 상세 정보를 담은 DTO")
public record OrderDetailDto(
        @Schema(description = "주문 상세 ID") Long id,
        @Schema(description = "약 Id") Long medicineId,
        @Schema(description = "약 이름") String medicineName,
        @Schema(description = "상품 코드") String productCode,
        @Schema(description = "이미지")   String imageURL,
        @Schema(description = "수량") int quantity,
        @Schema(description = "총 가격") int totalPrice
) {
    public static OrderDetailDto of(Long id, Long medicineId, String medicineName, String productCode, String imageURL, int quantity, int totalPrice) {
        return new OrderDetailDto(id, medicineId,medicineName, productCode, imageURL, quantity, totalPrice);
    }

    public static OrderDetailDto from(OrderDetail orderDetail) {
        return new OrderDetailDto(
                orderDetail.getId(),
                orderDetail.getMedicine().getId(),
                orderDetail.getMedicine().getName(),
                orderDetail.getMedicine().getProductCode(),
                orderDetail.getMedicine().getImageURL(),
                orderDetail.getQuantity(),
                orderDetail.getTotalPrice()
        );
    }
}