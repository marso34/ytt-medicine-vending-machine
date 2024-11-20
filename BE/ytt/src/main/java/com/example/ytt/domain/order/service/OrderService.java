package com.example.ytt.domain.order.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.exception.InventoryException;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderDetail;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.dto.*;
import com.example.ytt.domain.order.exception.OrderException;
import com.example.ytt.domain.order.repository.OrderRepository;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // TODO: 수령완료 과정중 사용자 수령했는지 확인 로직 필요, 동시성 제어 필요 ( 자판기 상태 , 보관함 현황 )

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final OrderDetailService orderDetailService;
    private final SimpMessagingTemplate messagingTemplate;

    private void notifyOrderUpdate(Order order) {
        OrderVendingMachineDto orderDto = OrderVendingMachineDto.from(order);
        String vendingMachineTopic = String.format("/topic/orders/vending-machine/%d", order.getVendingMachine().getId());
        String orderStoreTopic = String.format("/topic/orders/store/%s", order.getId());

        messagingTemplate.convertAndSend(vendingMachineTopic, orderDto);
        messagingTemplate.convertAndSend(orderStoreTopic, orderDto);
    }


    /**
     * 주문 생성
     * @param orderReqDto
     * @return
     */
    @Transactional
    public OrderDto createOrder(OrderReqDto orderReqDto) {
        User user = userRepository.findById(orderReqDto.userId())
                .orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));

        VendingMachine vendingMachine = vendingMachineRepository.findById(orderReqDto.vendingMachineId())
                .orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        List<OrderDetail> orderDetails = processOrderDetails(orderReqDto.orderItems(), vendingMachine);

        Order savedOrder = Order.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .orderItems(orderDetails)
                .build();

        orderRepository.save(savedOrder);
        vendingMachineRepository.save(vendingMachine);
        notifyOrderUpdate(savedOrder);
        return OrderDto.from(savedOrder);
    }


    /**
     * 자판기의 보관 결과 처리
     * @param orderId
     * @param storeResult 보관 성공 여부
     * @return
     */
    @Transactional
    public OrderDto processStoreResult(UUID orderId, OrderStoreResDto storeResult) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (storeResult.result()) {
            order.setOrderState(OrderState.STORED);
        } else {
            order.setOrderState(OrderState.CANCELED);
            restoreInventoryByActualStored(order, storeResult.orderItems());
        }

        Order savedOrder = orderRepository.save(order);
        notifyOrderUpdate(savedOrder);
        return OrderDto.from(savedOrder);
    }

    /**
     * 수령 완료
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (order.getOrderState() != OrderState.STORED) {
            throw new OrderException(ExceptionType.INVALID_ORDER_STATE_STORED);
        }

        order.setOrderState(OrderState.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());


        Order savedOrder = orderRepository.save(order);
        notifyOrderUpdate(savedOrder);
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
                .orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (order.getOrderState() != OrderState.PENDING) {
            throw new OrderException(ExceptionType.INVALID_ORDER_STATE_PENDING);
        }

        order.setOrderState(OrderState.CANCELED);

        restoreInventory(order);

        Order savedOrder = orderRepository.save(order);
        notifyOrderUpdate(savedOrder);
        return OrderDto.from(savedOrder);
    }


    private void restoreInventory(Order order) {
        VendingMachine vendingMachine = order.getVendingMachine();

        order.getOrderItems().forEach(orderDetail -> {
            Long medicineId = orderDetail.getMedicine().getId();
            Inventory inventory = inventoryRepository.findByMedicineIdAndVendingMachineId(medicineId, vendingMachine.getId())
                    .orElseThrow(() -> new InventoryException(ExceptionType.NOT_FOUND_INVENOTRY));
            inventoryRepository.save(inventory.addQuantity(orderDetail.getQuantity()));
        } );
    }

    private void restoreInventoryByActualStored(Order order, List<OrderDetailReqDto> actualStoredItems) {
        VendingMachine vendingMachine = order.getVendingMachine();
        Map<String, Integer> actualStoredMap = actualStoredItems.stream()
                .collect(Collectors.toMap(
                        OrderDetailReqDto::productCode,
                        OrderDetailReqDto::quantity
                ));

        order.getOrderItems().forEach(orderItem -> {
            String productCode = orderItem.getMedicine().getProductCode();
            int requestedQuantity = orderItem.getQuantity();
            int actualStoredQuantity = actualStoredMap.getOrDefault(productCode, 0);
            Long medicineId = orderItem.getMedicine().getId();

            // 요청 수량과 실제 보관 수량의 차이만큼 재고 복구
            int quantityToRestore = requestedQuantity - actualStoredQuantity;

            if (quantityToRestore > 0) {
                Inventory inventory = inventoryRepository.findByMedicineIdAndVendingMachineId(medicineId, vendingMachine.getId())
                        .orElseThrow(() -> new InventoryException(ExceptionType.NOT_FOUND_INVENOTRY));
                inventoryRepository.save(inventory.setQuantity(inventory.getQuantity() + quantityToRestore));
            }
        });

        vendingMachineRepository.save(vendingMachine);
    }

    private List<OrderDetail> processOrderDetails(List<OrderDetailReqDto> items, VendingMachine vendingMachine) {
        return items.stream()
                .map(item -> {
                    OrderDetail detail = orderDetailService.createOrderDetail(item);
                    updateInventory(vendingMachine, detail);
                    return detail;
                })
                .toList();
    }

    private void updateInventory(VendingMachine vendingMachine, OrderDetail orderDetail) {
        vendingMachine.getInventories().stream()
                .filter(inventory -> inventory.getMedicine().equals(orderDetail.getMedicine()))
                .findFirst()
                .ifPresentOrElse(
                        inventory -> updateInventoryQuantity(inventory, orderDetail),
                        () -> {
                            throw new OrderException(ExceptionType.NOT_FOUND_INVENOTRY);
                        });


    }

    private void updateInventoryQuantity(Inventory inventory, OrderDetail orderDetail) {
        int newQuantity = inventory.getQuantity() - orderDetail.getQuantity();
        if (newQuantity < 0) {
            throw new OrderException(ExceptionType.INSUFFICIENT_INVENTORY);
        }
        inventory.setQuantity(newQuantity);
    }

    public List<OrderDto> getCurrentOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderDto::from)
                .toList();
    }

    public List<OrderVendingMachineDto> getCurrentOrdersForVendingMachine(Long vendingMachineId) {
        List<Order> orders = orderRepository.findByVendingMachineId(vendingMachineId);
        return orders.stream()
                .map(OrderVendingMachineDto::from)
                .toList();
    }
}
