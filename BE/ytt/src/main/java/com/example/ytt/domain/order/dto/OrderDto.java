package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(title = "주문 Response DTO", description = "주문 정보를 담은 DTO")
public record OrderDto(
        @Schema(description = "주문 UUID") UUID id,
        @Schema(description = "주문자 ID") Long userId,
        @Schema(description = "주문자 이름") String userName,
        @Schema(description = "자판기 ID")String vendingMachineName,
        @Schema(description = "주문 상태") OrderState orderState,
        @Schema(description = "주문 시간") LocalDateTime orderAt,
        @Schema(description = "총 가격") int totalPrice,
        @Schema(description = "주문 상세 목록") List<OrderDetailDto> orderItems
) {
    public static OrderDto of(UUID id, Long userId, String userName, String vendingMachineName, OrderState orderState,
                              LocalDateTime orderAt, int totalPrice, List<OrderDetailDto> orderItems) {
        return new OrderDto(id, userId, userName, vendingMachineName, orderState, orderAt, totalPrice, orderItems);
    }

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getVendingMachine().getName(),
                order.getOrderState(),
                order.getOrderAt(),
                order.getTotalPrice(),
                order.getOrderItems().stream()
                        .map(OrderDetailDto::from)
                        .toList()
        );
    }

}
