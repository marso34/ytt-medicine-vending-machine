package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.InboundLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboundLogRepository extends JpaRepository<InboundLog, Long> {

    List<InboundLog> findByVendingMachineId(Long machineId);

    List<InboundLog> findByVendingMachineIdAndMedicineId(Long vendingMachineId, Long medicineId);

}