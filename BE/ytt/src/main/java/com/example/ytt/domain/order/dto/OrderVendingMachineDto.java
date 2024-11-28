package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(title = "자판기 주문 DTO", description = "서버에서 자판기로 주문생성 정보 DTO")
public record OrderVendingMachineDto(
        @Schema(description = "UUID")        String id,
        @Schema(description = "유저 ID")      Long userId,
        @Schema(description = "주문 상태")     OrderState orderState,
        @Schema(description = "주문 시간")     LocalDateTime orderAt,
        @Schema(description = "주문 상세 목록") List<OrderItemReqDto> orderItems
){
    public static OrderVendingMachineDto of(UUID id, Long userId, OrderState orderState, LocalDateTime orderAt, List<OrderDetailReqDto> orderItems) {
        return new OrderVendingMachineDto(id.toString(), userId, orderState, orderAt, orderItems);
    }

    public static OrderVendingMachineDto from(Order order) {
        List<OrderDetailReqDto> orderDetailDtos = order.getOrderItems().stream()
                .map(detail -> new OrderDetailReqDto(
                        detail.getMedicine().getProductCode(),
                        detail.getQuantity()
                ))
                .toList();

        return OrderVendingMachineDto.of(
                order.getId(),
                order.getUser().getId(),
                order.getOrderState(),
                order.getOrderAt(),
                orderDetailDtos
        );
    }

}
