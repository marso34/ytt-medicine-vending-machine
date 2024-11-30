package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(title = "주문 Response DTO", description = "주문 정보를 담은 DTO")
public record OrderDto(
        @Schema(description = "주문 번호")   String id,
        @Schema(description = "자판기 ID")  Long machineId,
        @Schema(description = "주문자 이름") String machineName,
        @Schema(description = "주문 상태")   OrderState orderState,
        @Schema(description = "주문 시간")   LocalDateTime orderAt,
        @Schema(description = "총 가격")    int totalPrice
) {

    public static OrderDto of(String id, Long machineId, String machineName, OrderState orderState, LocalDateTime orderAt, int totalPrice) {
        return new OrderDto(id, machineId, machineName, orderState, orderAt, totalPrice);
    }

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId().toString(),
                order.getVendingMachine().getId(),
                order.getVendingMachine().getName(),
                order.getOrderState(),
                order.getOrderAt(),
                order.getTotalPrice()
        );
    }

}
