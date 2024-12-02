package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    boolean existsByIDAndUserId(String id, Long userId);

    List<Order> getOrders(Long userId, Long machineId, OrderState state);

    Optional<Order> getOrderDetail(String orderId);

    Long getOrderCount(Long machineId, OrderState state);

}
