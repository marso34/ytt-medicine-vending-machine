package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMedicineIdAndVendingMachineId(Long medicineId, Long vendingMachineId); // 약품 ID와 자판기 ID로 검색 (특정 자판기에 특정 약품)

    List<Inventory> findByVendingMachineId(Long vendingMachineId); // 자판기 ID로 검색 (특정 자판기의 재고 목록 조회)

    List<Inventory> findByMedicineId(Long medicineId); // 약품 ID로 검색 (특정 약품을 가지고 있는 재고 목록 (자판기) 조회)

    void deleteByVendingMachineIdAndMedicineId(Long vendingMachineId, Long medicineId); // 자판기 ID와 약품 ID로 삭제 (특정 자판기의 특정 약품 삭제)

}
