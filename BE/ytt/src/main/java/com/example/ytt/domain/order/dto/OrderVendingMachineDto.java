package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(title = "자판기 주문 DTO", description = "서버에서 자판기로 주문생성 정보 DTO")
public record OrderVendingMachineDto(
        @Schema(description = "UUID") UUID id,
        @Schema(description = "주문 상태")OrderState orderState,
        @Schema(description = "주문 시간") LocalDateTime orderAt,
        @Schema(description = "주문 상세 목록") List<OrderDetailVendingMachineDto> orderItems
){
    public static OrderVendingMachineDto of(UUID id, OrderState orderState, LocalDateTime orderAt, List<OrderDetailVendingMachineDto> orderItems) {
        return new OrderVendingMachineDto(id, orderState, orderAt, orderItems);
    }

    public static OrderVendingMachineDto from(Order order) {
        List<OrderDetailVendingMachineDto> orderDetailDtos = order.getOrderItems().stream()
                .map(detail -> new OrderDetailVendingMachineDto(
                        detail.getMedicine().getProductCode(),
                        detail.getQuantity()
                ))
                .toList();

        return OrderVendingMachineDto.of(
                order.getId(),
                order.getOrderState(),
                order.getOrderAt(),
                orderDetailDtos
        );
    }

}
