package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderItem, Integer> {
}
