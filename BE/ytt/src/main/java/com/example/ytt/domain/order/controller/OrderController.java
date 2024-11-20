package com.example.ytt.domain.order.controller;

import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.order.service.OrderService;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문 기본", description = "주문 생성, API")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    @SwaggerApi(summary = "사용자의 주문 목록 조회", description = "로그인된 사용자의 모든 주문 목록을 조회합니다.", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<List<OrderDto>>> getOrdersForUser(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseUtil.success(orderService.getCurrentOrdersForUser(user.getId()));
    }

    @PostMapping("/create")
    @SwaggerApi(summary = "주문 생성", description = "주문 생성 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> createOrder(@RequestBody OrderReqDto reqDto) {
        return ResponseUtil.success(orderService.createOrder(reqDto));
    }

    @PostMapping("/complete/{orderId}")
    @SwaggerApi(summary = "자판기에서 약 수령 완료", description = "사용자가 약 수령 후 주문 완료 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> completeOrder(@PathVariable @DestinationVariable UUID orderId) {
        return ResponseUtil.success(orderService.completeOrder(orderId));
    }

    @PostMapping("/cancel/{orderId}")
    @SwaggerApi(summary = "주문 취소", description = "주문 취소 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> cancelOrder(@PathVariable @DestinationVariable UUID orderId) {
        return ResponseUtil.success(orderService.cancelOrder(orderId));
    }


}
