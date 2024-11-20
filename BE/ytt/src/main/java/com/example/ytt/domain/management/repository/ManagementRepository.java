package com.example.ytt.domain.management.repository;

import com.example.ytt.domain.management.domain.Management;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementRepository extends JpaRepository<Management, Long>, ManagementRepositoryCustom {

    boolean existsByUserIdAndVendingMachineId(Long userId, Long vendingMachineId);

    void deleteByUserIdAndVendingMachineId(Long userId, Long vendingMachineId);

}
