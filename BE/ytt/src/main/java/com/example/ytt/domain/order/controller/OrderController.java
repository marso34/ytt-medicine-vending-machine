package com.example.ytt.domain.order.controller;


import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.order.dto.OrderVendingMachineDto;
import com.example.ytt.domain.order.service.OrderService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문 기본", description = "주문 생성, API")
public class OrderController {

    private final OrderService orderService;

    @MessageMapping("/current-orders/user")
    @SendTo("/topic/current-orders/user")
    public List<OrderDto> getCurrentOrdersForUser() {
        return orderService.getCurrentOrdersForUser();
    }

    @MessageMapping("/current-orders/vending-machine")
    @SendTo("/topic/current-orders/vending-machine")
    public List<OrderVendingMachineDto> getCurrentOrdersForVendingMachine() {
        return orderService.getCurrentOrdersForVendingMachine();
    }
    @PostMapping("/create")
    @MessageMapping("/order/{vendingMachineId}")
    @SendTo("/topic/order/{vendingMachineId}")
    @SwaggerApi(summary = "주문 생성", description = "주문 생성 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> createOrder(@RequestBody OrderReqDto reqDto) {
        return ResponseUtil.success(orderService.createOrder(reqDto));
    }

    @PostMapping("/store/{orderId}")
    @MessageMapping("/order/store/{orderId}")
    @SendTo("/topic/order/store/{orderId}")
    @SwaggerApi(summary = "주문 보관 완료", description = "자판기에서 주문 보관 완료 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> storeOrder(@PathVariable @DestinationVariable UUID orderId) {
        return ResponseUtil.success(orderService.storeOrder(orderId));
    }

    @PostMapping("/complete/{orderId}")
    @MessageMapping("/order/complete/{orderId}")
    @SendTo("/topic/order/complete/{orderId}")
    @SwaggerApi(summary = "주문 완료", description = "사용자가 약 수령 후 주문 완료 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> completeOrder(@PathVariable @DestinationVariable UUID orderId) {
        return ResponseUtil.success(orderService.completeOrder(orderId));
    }

    @PostMapping("/cancel/{orderId}")
    @MessageMapping("/order/cancel/{orderId}")
    @SendTo("/topic/order/cancel/{orderId}")
    @SwaggerApi(summary = "주문 취소", description = "주문 취소 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> cancelOrder(@PathVariable @DestinationVariable UUID orderId) {
        return ResponseUtil.success(orderService.cancelOrder(orderId));
    }


}
