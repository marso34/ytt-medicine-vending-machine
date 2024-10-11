package com.example.ytt.domain.vendingmachine.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendingMachineRepository extends JpaRepository<VendingMachine, Long> {
    List<VendingMachine> findByNameContaining(String name);
}
