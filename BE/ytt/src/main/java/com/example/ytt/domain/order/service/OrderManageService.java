package com.example.ytt.domain.order.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.exception.InventoryException;
import com.example.ytt.domain.inventory.service.InventoryService;
import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderItem;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.dto.*;
import com.example.ytt.domain.order.dto.machine.OrderResultDto;
import com.example.ytt.domain.order.dto.machine.OrderVendingMachineDto;
import com.example.ytt.domain.order.dto.request.OrderItemReqDto;
import com.example.ytt.domain.order.dto.request.OrderReqDto;
import com.example.ytt.domain.order.exception.OrderException;
import com.example.ytt.domain.order.repository.OrderRepository;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.service.UserService;
import com.example.ytt.domain.vendingmachine.service.VendingMachineFindService;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderManageService {

    private final OrderRepository orderRepository;

    private final VendingMachineFindService vendingMachineFindService;
    private final InventoryService inventoryService;
    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;

    private void notifyOrderToVendingMachine(Order order) {
        OrderVendingMachineDto orderDto = OrderVendingMachineDto.from(order);
        String vendingMachineTopic = String.format("/topic/orders/vending-machine/%d", order.getVendingMachine().getId());

        messagingTemplate.convertAndSend(vendingMachineTopic, orderDto);
    }

    private void notifyOrderToUser(OrderResultDto order) {
        String orderStoreTopic = String.format("/topic/orders/store/%s", order.id());

        messagingTemplate.convertAndSend(orderStoreTopic, order);
    }

    /**
     * 주문 생성
     * @param orderReqDto
     * @return
     */
    @Transactional
    public OrderDetailDto createOrder(OrderReqDto orderReqDto) {
        // TODO: 동시성 제어 필요
        User user = userService.getUser(orderReqDto.userId());
        VendingMachine vendingMachine = vendingMachineFindService.getVendingMachine(orderReqDto.vendingMachineId());

        List<String> productCodes = getProductCodesByOrderReq(orderReqDto.orderItems());
        List<Inventory> inventories = inventoryService.getInventories(vendingMachine.getId(), productCodes); // 필요한 inventory만 조회

        if (productCodes.size() != inventories.size()) {
            throw new OrderException(ExceptionType.NOT_FOUND_MEDICINE);
        }

        List<OrderItem> orderItems = createOrderItems(orderReqDto.orderItems(), inventories);

        Order savedOrder = Order.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .orderItems(orderItems)
                .build();

        long n = orderRepository.getOrderCount(vendingMachine.getId(), OrderState.PENDING);

        if (n >= vendingMachine.getCapacity()) {
            throw new OrderException(ExceptionType.FULL_VENDING_MACHINE_ORDER);
        }

        orderRepository.save(savedOrder);
        updateInventories(orderReqDto.orderItems(), inventories);

        notifyOrderToVendingMachine(savedOrder);

        return OrderDetailDto.from(savedOrder);
    }

    /**
     * 자판기의 보관 결과 처리
     * @param orderId
     * @param storeResult 보관 성공 여부
     * @return
     */
    @Transactional
    public OrderResultDto processStoreResult(String orderId, OrderResultDto storeResult) {
        Order order = orderRepository.getOrderDetail(orderId).orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (storeResult.result()) {
            order.setOrderState(OrderState.STORED);
        } else {
            order.setOrderState(OrderState.CANCELED);
            restoreInventoryByActualStored(order, storeResult.orderItems());
        }

        order.setCompletedAt();
        Order savedOrder = orderRepository.save(order);
        OrderResultDto result = OrderResultDto.of(savedOrder, storeResult.result());

        notifyOrderToUser(result);
        return result;
    }

    /**
     * 수령 완료
     * @param orderId
     * @return
     */
    @Transactional
    public OrderResultDto completeOrder(String orderId) {
        Order order = orderRepository.getOrderDetail(orderId).orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (order.getOrderState() != OrderState.STORED) {
            throw new OrderException(ExceptionType.INVALID_ORDER_STATE_STORED);
        }

        order.setCompletedAt();
        Order savedOrder = orderRepository.save(order.setOrderState(OrderState.COMPLETED));
        OrderResultDto result = OrderResultDto.of(savedOrder, true);

        notifyOrderToUser(result);
        return result;
    }

    /**
     * 주문 취소
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDetailDto cancelOrder(String orderId) {
        // TODO: 이미 자판기에 넘어간 주문은 취소 불가능해야 함
        Order order = orderRepository.getOrderDetail(orderId).orElseThrow(() -> new OrderException(ExceptionType.NOT_FOUND_ORDER));

        if (order.getOrderState() != OrderState.PENDING) {
            throw new OrderException(ExceptionType.INVALID_ORDER_STATE_PENDING);
        }

        restoreInventory(order);

        order.setCompletedAt();
        Order savedOrder = orderRepository.save(order.setOrderState(OrderState.CANCELED));
        notifyOrderToVendingMachine(savedOrder);

        return OrderDetailDto.from(savedOrder);
    }

    private List<String> getProductCodesByOrderReq(List<OrderItemReqDto> orderItems) {
        return orderItems.stream()
                .map(OrderItemReqDto::productCode)
                .toList();
    }

    private List<String> getProductCodesByOrder(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getMedicine().getProductCode())
                .toList();
    }

    private List<OrderItem> createOrderItems(List<OrderItemReqDto> items, List<Inventory> inventories) {
        return IntStream.range(0, items.size())
                .mapToObj(i -> {
                    int orderQuantity = items.get(i).quantity();

                    if (inventories.get(i).getQuantity() < orderQuantity) {
                        throw new InventoryException(ExceptionType.INSUFFICIENT_INVENTORY);
                    }

                    return OrderItem.of(inventories.get(i).getMedicine(), orderQuantity);
                })
                .toList();
    }

    public void updateInventories(List<OrderItemReqDto> items, List<Inventory> inventories) {
        for (int i = 0; i < items.size(); i++) {
            int orderQuantity = items.get(i).quantity();
            inventoryService.update(inventories.get(i).removeQuantity(orderQuantity));
        }
    }

    private void restoreInventory(Order order) {
        VendingMachine vendingMachine = order.getVendingMachine();

        List<OrderItem> orderItems = order.getOrderItems();
        List<String> productCodes = getProductCodesByOrder(orderItems);
        List<Inventory> inventories = inventoryService.getInventories(vendingMachine.getId(), productCodes); // 필요한 inventory만 조회

        for (int i = 0; i < orderItems.size(); i++) {
            int orderQuantity = orderItems.get(i).getQuantity();
            inventoryService.update(inventories.get(i).addQuantity(orderQuantity));
        }
    }

    private void restoreInventoryByActualStored(Order order, List<OrderItemReqDto> actualStoredItems) {
        VendingMachine vendingMachine = order.getVendingMachine();

        List<OrderItem> orderItems = order.getOrderItems();
        List<String> productCodes = getProductCodesByOrder(orderItems);
        List<Inventory> inventories = inventoryService.getInventories(vendingMachine.getId(), productCodes); // 필요한 inventory만 조회

        for (int i = 0; i < orderItems.size(); i++) {
            int orderQuantity = orderItems.get(i).getQuantity();
            int actualStoredQuantity = actualStoredItems.get(i).quantity();

            // 요청 수량과 실제 보관 수량의 차이만큼 재고 복구
            int quantityToRestore = orderQuantity - actualStoredQuantity;

            inventoryService.update(inventories.get(i).setQuantity(inventories.get(i).getQuantity() + quantityToRestore));
        }
    }

}
