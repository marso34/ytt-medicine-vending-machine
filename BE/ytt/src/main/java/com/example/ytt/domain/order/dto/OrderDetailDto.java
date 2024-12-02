package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(title = "주문 상세 Response DTO", description = "주문 정보를 담은 DTO")
public record OrderDetailDto(
        @Schema(description = "주문 번호")     String id,
        @Schema(description = "주문자 ID")     Long userId,
        @Schema(description = "주문자 이름")    String userName,
        @Schema(description = "자판기 ID")     Long machineId,
        @Schema(description = "자판기 이름")    String machineName,
        @Schema(description = "주문 상태")     OrderState orderState,
        @Schema(description = "주문 시간")     LocalDateTime orderAt,
        @Schema(description = "주문 완료 시간") LocalDateTime completedAt,
        @Schema(description = "총 가격")      int totalPrice,
        @Schema(description = "주문 상세 목록") List<OrderItemDto> orderItems
) {

    public static OrderDetailDto of(String id, Long userId, String userName, Long machineId, String machineName, OrderState orderState, LocalDateTime orderAt, LocalDateTime completedAt, int totalPrice, List<OrderItemDto> orderItems) {
        return new OrderDetailDto(id, userId, userName, machineId, machineName, orderState, orderAt, completedAt, totalPrice, orderItems);
    }

    public static OrderDetailDto from(Order order) {
        return new OrderDetailDto(
                order.getId().toString(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getVendingMachine().getId(),
                order.getVendingMachine().getName(),
                order.getOrderState(),
                order.getOrderAt(),
                order.getCompletedAt(),
                order.getTotalPrice(),
                order.getOrderItems().stream().map(OrderItemDto::from).toList()
        );
    }

}
