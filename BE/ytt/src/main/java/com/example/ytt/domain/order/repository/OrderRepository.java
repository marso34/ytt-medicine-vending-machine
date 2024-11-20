package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(Long userId);

    List<Order> findByVendingMachineId(Long vendingMacingId);

    Optional<Order> findById(UUID id);
}
