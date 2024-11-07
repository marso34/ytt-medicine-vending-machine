package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "주문 Response DTO", description = "주문 정보를 담은 DTO")
public record OrderDto(
        @Schema(description = "주문 ID") Long id,
        @Schema(description = "주문자 ID") User user,
        @Schema(description = "자판기 ID")VendingMachine vendingMachine,
        @Schema(description = "주문 상태") OrderState orderState,
        @Schema(description = "주문 시간") LocalDateTime orderAt,
        @Schema(description = "완료 시간") LocalDateTime completedAt,
        @Schema(description = "총 가격") int totalPrice,
        @Schema(description = "주문 상세 목록") List<OrderDetailDto> orderItems
) {
    public static OrderDto of(Long id, User user, VendingMachine vendingMachine, OrderState orderState,
                              LocalDateTime orderAt, LocalDateTime completedAt, int totalPrice, List<OrderDetailDto> orderItems) {
        return new OrderDto(id, user, vendingMachine, orderState, orderAt, completedAt, totalPrice, orderItems);
    }

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUser(),
                order.getVendingMachine(),
                order.getOrderState(),
                order.getOrderAt(),
                order.getCompletedAt(),
                order.getTotalPrice(),
                order.getOrderItems().stream()
                        .map(OrderDetailDto::from)
                        .collect(Collectors.toList())
        );
    }
}
