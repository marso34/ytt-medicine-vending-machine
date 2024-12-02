package com.example.ytt.domain.order.dto.machine;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.dto.request.OrderItemReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

public record OrderResultDto(
        @Schema(description = "UUID")        String id,
        @Schema(description = "주문 결과")     boolean result,
        @Schema(description = "주문 상태")     OrderState orderState,
        @Schema(description = "주문 상세 목록") List<OrderItemReqDto> orderItems
) {
    public static OrderResultDto of(UUID id, boolean result, OrderState orderState, List<OrderItemReqDto> orderItems) {
        return new OrderResultDto(id.toString(), result, orderState, orderItems);
    }

    public static OrderResultDto of(Order order, boolean result) {
        return new OrderResultDto(
                order.getId().toString(),
                result,
                order.getOrderState(),
                order.getOrderItems().stream()
                        .map(OrderItemReqDto::from)
                        .toList()
        );
    }

}
