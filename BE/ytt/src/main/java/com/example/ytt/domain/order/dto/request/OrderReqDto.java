package com.example.ytt.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(title = "주문 생성 Request DTO", description = "주문 생성 요청 DTO")
public record OrderReqDto(
        @Schema(description = "사용자 Id")   Long userId,
        @Schema(description = "자판기 Id")   Long vendingMachineId,
        @Schema(description = "상품 리스트")  List<OrderItemReqDto> orderItems
) {
}