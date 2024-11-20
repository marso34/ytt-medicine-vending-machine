package com.example.ytt.domain.order.controller;

import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderStoreResDto;
import com.example.ytt.domain.order.dto.OrderVendingMachineDto;
import com.example.ytt.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class OrderWebSocketController {

    private final OrderService orderService;

    @MessageMapping("/order/store/{orderId}")
    @SendTo("/topic/orders/store/{orderId}")
    public OrderStoreResDto handleStoreOrder(
            @DestinationVariable UUID orderId,
            @Payload OrderStoreResDto storeResult
    ) {
        OrderDto updatedOrder = orderService.processStoreResult(orderId, storeResult);
        return OrderStoreResDto.from(updatedOrder, storeResult.result());
    }

    // 사용자에서 수령완료 수신
    @MessageMapping("/order/complete/{orderId}")
    @SendTo("/order/complete/{orderId}")
    public void handleCompleteOrder(@DestinationVariable UUID orderId) {
        orderService.completeOrder(orderId);
    }

    // 사용자에서 주문취소 수신
    @MessageMapping("/order/cancel/{orderId}")
    @SendTo("/order/complete/{orderId}")
    public void handleCancelOrder(@DestinationVariable UUID orderId) {
        orderService.cancelOrder(orderId);
    }

    // 리스트를 받는경우 사용
    @MessageMapping("/orders/vending-machine/{vendingMachineId}/current")
    @SendTo("/topic/orders/vending-machine/{vendingMachineId}")
    public List<OrderVendingMachineDto> getCurrentOrders(@DestinationVariable Long vendingMachineId) {
        return orderService.getCurrentOrdersForVendingMachine(vendingMachineId);
    }
}
