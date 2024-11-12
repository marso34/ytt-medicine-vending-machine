package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Order findByUserId(Long userId);

    Optional<Order> findById(UUID id);
}
