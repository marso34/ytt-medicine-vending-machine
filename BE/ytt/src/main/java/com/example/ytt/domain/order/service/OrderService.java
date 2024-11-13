package com.example.ytt.domain.order.service;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderDetail;
import com.example.ytt.domain.order.domain.OrderState;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // TODO: 예외처리 형식 맞추기
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final OrderDetailService orderDetailService;
    private final SimpMessagingTemplate messagingTemplate;

    private void sendOrderToVendingMachine(Long vendingMachineId, OrderReqDto orderReqDto) {
        // STOMP 메시징 템플릿을 사용하여 자판기로 주문 전송
        messagingTemplate.convertAndSend("/topic/vending-machine/" + vendingMachineId, orderReqDto);
    }

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

        // 자판기로 주문 전송
        sendOrderToVendingMachine(vendingMachine.getId(), orderReqDto);

        orderRepository.save(order);
        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getOrderState() != OrderState.PENDING) {
            throw new IllegalStateException("대기 중인 주문만 완료할 수 있습니다.");
        }

        order.setOrderState(OrderState.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        VendingMachine vendingMachine = order.getVendingMachine();

        for (OrderDetail orderItem : order.getOrderItems()) {
            vendingMachine.getInventories().stream()
                    .filter(inventory -> inventory.getMedicine().equals(orderItem.getMedicine()))
                    .findFirst()
                    .ifPresent(inventory -> {
                        int newQuantity = inventory.getQuantity() - orderItem.getQuantity();
                        if (newQuantity < 0) {
                            throw new IllegalStateException(orderItem.getMedicine().getName() + "의 재고가 부족합니다.");
                        }
                        inventory.setQuantity(newQuantity);
                    });
        }

        vendingMachineRepository.save(vendingMachine);
        Order savedOrder = orderRepository.save(order);
        return OrderDto.from(savedOrder);
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getOrderState() == OrderState.COMPLETED) {
            throw new IllegalStateException("이미 완료된 주문은 취소할 수 없습니다.");
        }

        order.setOrderState(OrderState.CANCELED);

        VendingMachine vendingMachine = order.getVendingMachine();

        for (OrderDetail orderItem : order.getOrderItems()) {
            vendingMachine.getInventories().stream()
                    .filter(inventory -> inventory.getMedicine().equals(orderItem.getMedicine()))
                    .findFirst()
                    .ifPresent(inventory -> inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity()));
        }

        vendingMachineRepository.save(vendingMachine);
        Order savedOrder = orderRepository.save(order);
        return OrderDto.from(savedOrder);
    }

    public void sendCurrentOrders() {
        List<OrderDto> currentOrders = orderRepository.findAll().stream()
                .map(OrderDto::from)
                .toList();

        messagingTemplate.convertAndSend("/topic/current-orders", currentOrders);
    }



}
