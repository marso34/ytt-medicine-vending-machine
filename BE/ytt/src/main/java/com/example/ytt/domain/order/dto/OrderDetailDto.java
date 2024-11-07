package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.order.domain.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "주문 상세 Response DTO", description = "주문 상세 정보를 담은 DTO")
public record OrderDetailDto(
        @Schema(description = "주문 상세 ID") Long id,
        @Schema(description = "약 정보") MedicineDto medicine,
        @Schema(description = "수량") int quantity,
        @Schema(description = "총 가격") int totalPrice
) {
    public static OrderDetailDto of(Long id, MedicineDto medicine, int quantity, int totalPrice) {
        return new OrderDetailDto(id, medicine, quantity, totalPrice);
    }

    public static OrderDetailDto from(OrderDetail orderDetail) {
        return new OrderDetailDto(
                orderDetail.getId(),
                MedicineDto.from(orderDetail.getMedicine()),
                orderDetail.getQuantity(),
                orderDetail.calculateTotalPrice()
        );
    }
}