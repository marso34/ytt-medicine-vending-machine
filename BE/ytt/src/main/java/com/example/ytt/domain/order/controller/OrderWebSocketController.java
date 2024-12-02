package com.example.ytt.domain.order.controller;

import com.example.ytt.domain.order.dto.machine.OrderResultDto;
import com.example.ytt.domain.order.service.OrderManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
public class OrderWebSocketController {

    private final OrderManageService orderManageService;

    // 자판기에서 주문 처리 결과 (보관 완료, 보관 실패) 송신, 유저 수신
    @MessageMapping("/order/store/{orderId}")
    @SendTo("/topic/orders/store/{orderId}")
    public OrderResultDto handleStoreOrder(
            @DestinationVariable("orderId") String orderId,
            @Payload OrderResultDto storeResult
    ) {
        return orderManageService.processStoreResult(orderId, storeResult);
    }

    // 자판기에서 보관함 수령 완료 송신, 유저 수신
    @MessageMapping("/order/complete/{orderId}")
    @SendTo("/order/complete/{orderId}")
    public OrderResultDto handleCompleteOrder(@DestinationVariable("orderId") String orderId) {
        return orderManageService.completeOrder(orderId);
    }

//     리스트를 받는경우 사용
//    @MessageMapping("/orders/vending-machine/{vendingMachineId}/current")
//    @SendTo("/topic/orders/vending-machine/{vendingMachineId}")
//    public List<OrderVendingMachineDto> getCurrentOrders(@DestinationVariable("orderId") Long vendingMachineId) {
//        return orderFindService.getCurrentOrdersForVendingMachine(vendingMachineId);
//    }
}
