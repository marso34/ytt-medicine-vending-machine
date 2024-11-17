package com.example.ytt.domain.order.service;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderDetail;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.dto.OrderDetailReqDto;
import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.order.dto.OrderVendingMachineDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // TODO: 예외처리 형식 맞추기, 값을 다르게 UUID, boolean, 주문완료 검증 로직 필요(사용자 수령 인증/Line:129)

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final OrderDetailService orderDetailService;
    private final SimpMessagingTemplate messagingTemplate;

    private void sendOrderToVendingMachine(Long vendingMachineId, OrderVendingMachineDto orderVendingMachineDto) {
        // STOMP 메시징 템플릿을 사용하여 자판기로 주문 전송
        messagingTemplate.convertAndSend("/topic/vending-machine/" + vendingMachineId, orderVendingMachineDto);
    }

    /**
     * 주문 생성
     * @param orderReqDto
     * @return
     */
    @Transactional
    public OrderDto createOrder(OrderReqDto orderReqDto) {
        // 사용자 조회
        User user = userRepository.findById(orderReqDto.userId())
                .orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));

        // 자판기 조회
        VendingMachine vendingMachine = vendingMachineRepository.findById(orderReqDto.vendingMachineId())
                .orElseThrow(() -> new IllegalArgumentException("자판기 ID를 찾을 수 없습니다: " + orderReqDto.vendingMachineId()));

        // 주문 항목 리스트 처리 및 재고 확인/감소
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailReqDto itemReqDto : orderReqDto.orderItems()) {
            OrderDetail orderDetail = orderDetailService.createOrderDetail(itemReqDto);

            // 재고 확인 및 감소
            vendingMachine.getInventories().stream()
                    .filter(inventory -> inventory.getMedicine().equals(orderDetail.getMedicine()))
                    .findFirst()
                    .ifPresentOrElse(inventory -> {
                        int newQuantity = inventory.getQuantity() - orderDetail.getQuantity();
                        if (newQuantity < 0) {
                            throw new IllegalStateException(orderDetail.getMedicine().getName() + "의 재고가 부족합니다.");
                        }
                        inventory.setQuantity(newQuantity);
                    }, () -> {
                        throw new IllegalStateException(orderDetail.getMedicine().getName() + "의 재고 정보를 찾을 수 없습니다.");
                    });

            orderDetails.add(orderDetail);
        }

        Order order = Order.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .orderItems(orderDetails)
                .build();

        // 자판기로 주문 전송
        sendOrderToVendingMachine(vendingMachine.getId(), OrderVendingMachineDto.from(order));

        orderRepository.save(order);
        vendingMachineRepository.save(vendingMachine);

        return OrderDto.from(order);
    }

    /**
     * 주문 보관중
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDto storeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getOrderState() != OrderState.PENDING) {
            throw new IllegalStateException("대기 중인 주문만 보관 완료할 수 있습니다.");
        }

        order.setOrderState(OrderState.STORED);
        Order savedOrder = orderRepository.save(order);
        return OrderDto.from(savedOrder);
    }

    /**
     * 주문 완료
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getOrderState() != OrderState.STORED) {
            throw new IllegalStateException("보관 중인 주문만 완료할 수 있습니다.");
        }

        // 사용자가 수령하는 인증 로직 추가
        
        order.setOrderState(OrderState.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());


        Order savedOrder = orderRepository.save(order);
        return OrderDto.from(savedOrder);
    }


    /**
     * 주문 취소
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getOrderState() != OrderState.PENDING) {
            throw new IllegalStateException("대기중인 주문만 취소할 수 있습니다.");
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

    public List<OrderDto> getCurrentOrdersForUser() {
        return orderRepository.findAll().stream()
                .map(OrderDto::from)
                .toList();
    }

    public List<OrderVendingMachineDto> getCurrentOrdersForVendingMachine() {
        return orderRepository.findAll().stream()
                .map(OrderVendingMachineDto::from)
                .toList();
    }

    @Transactional
    public void sendOrderToVendingMachine(UUID orderId) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 자판기로 전송할 DTO 생성
        OrderVendingMachineDto orderVendingMachineDto = OrderVendingMachineDto.from(order);

        // 자판기로 데이터 전송
        sendOrderToVendingMachine(order.getVendingMachine().getId(), orderVendingMachineDto);
    }



}
