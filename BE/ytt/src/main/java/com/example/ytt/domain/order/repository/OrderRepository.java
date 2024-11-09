package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByUserId(Long userId);

    Optional<Order> findById(Long id);
}
