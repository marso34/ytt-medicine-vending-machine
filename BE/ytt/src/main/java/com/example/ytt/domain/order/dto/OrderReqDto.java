package com.example.ytt.domain.order.dto;

import com.example.ytt.domain.order.domain.OrderDetail;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "주문 생성 Request DTO", description = "주문 생성 요청 DTO")
public record OrderReqDto(
        @Schema(description = "사용자 Id")   User userId,
        @Schema(description = "자판기 Id")   VendingMachine bendingMachineId,
        @Schema(description = "총 가격"  )   int price,
        @Schema(description = "상품 리스트") List<OrderDetail> orderItems
) {
}