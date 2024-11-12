package com.example.ytt.domain.order.service;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderDetail;
import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.order.repository.OrderRepository;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final OrderDetailService orderDetailService;

    @Transactional
    public OrderDto createOrder(OrderReqDto orderReqDto) {
        // 사용자 조회
        User user = userRepository.findById(orderReqDto.userId())
                .orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));

        // 자판기 조회
        VendingMachine vendingMachine = vendingMachineRepository.findById(orderReqDto.vendingMachineId())
                .orElseThrow(() -> new IllegalArgumentException("자판기 ID를 찾을 수 없습니다: " + orderReqDto.vendingMachineId()));

        // 주문 항목 리스트 처리
        List<OrderDetail> orderDetails = orderReqDto.orderItems().stream()
                .map(orderDetailService::createOrderDetail)
                .toList();

        Order order = Order.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .orderItems(orderDetails)
                .build();

        orderRepository.save(order);
        return OrderDto.from(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.cancelOrder();
    }

}
