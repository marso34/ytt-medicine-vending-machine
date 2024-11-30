package com.example.ytt.domain.order.service;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.dto.OrderDetailDto;
import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.exception.OrderException;
import com.example.ytt.domain.order.repository.OrderRepository;
import com.example.ytt.global.error.code.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderFindService {

    private final OrderRepository orderRepository;

    // 주문 내역 조회 (주문자)
    public List<OrderDto> getOrdersByUser(Long userId, String state) {
        OrderState orderState = (state == null) ? null : OrderState.from(state);

        List<Order> orders = orderRepository.getOrders(userId, null, orderState);

        if (orders.isEmpty()) {
            throw new OrderException(ExceptionType.NO_CONTENT_ORDER);
        }

        return orders.stream().map(OrderDto::from).toList();
    }

    // 자판기별 주문 내역 조회
    public List<OrderDto> getOrdersByVendingMachine(Long vendingMachineId, String state) {
        OrderState orderState = (state == null) ? null : OrderState.from(state);

        List<Order> orders = orderRepository.getOrders(null, vendingMachineId, orderState);

        if (orders.isEmpty()) {
            throw new OrderException(ExceptionType.NO_CONTENT_ORDER);
        }

        return orders.stream().map(OrderDto::from).toList();
    }

    // 주문 상세 조회 (주문자)
    public OrderDetailDto getOrderDetail(String orderId) {
        Order order = orderRepository.getOrderDetail(orderId)
                .orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        return OrderDetailDto.from(order);
    }

    // 주문자 권한 확인
    public boolean isOrderOwner(String orderId, Long userId) {
        return orderRepository.existsByIDAndUserId(orderId, userId);
    }

}
