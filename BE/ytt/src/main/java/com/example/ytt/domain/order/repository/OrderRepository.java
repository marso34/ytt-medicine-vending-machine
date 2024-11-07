package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByUserId(int userId);
}
