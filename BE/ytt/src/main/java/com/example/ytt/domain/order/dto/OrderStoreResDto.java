package com.example.ytt.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

public record OrderStoreResDto(
        @Schema(description = "UUID") UUID id,
        @Schema(description = "주문 상세 목록") List<OrderDetailReqDto> orderItems,
        @Schema(description = "주문 결과") boolean result
) {
    public static OrderStoreResDto of(UUID id, List<OrderDetailReqDto> orderItems, boolean result) {
        return new OrderStoreResDto(id, orderItems, result);
    }
    public static OrderStoreResDto from(OrderDto orderDto, boolean result) {
        return new OrderStoreResDto(
                orderDto.id(),
                orderDto.orderItems().stream()
                        .map(orderDetailDto -> OrderDetailReqDto.of(
                                orderDetailDto.productCode(),
                                orderDetailDto.quantity()
                        ))
                        .toList(),
                result
        );
    }

}
