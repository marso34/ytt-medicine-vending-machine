package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByMedicineId(Long medicineId); // 약품 ID로 검색 (특정 약품을 가지고 있는 재고 목록 (자판기) 조회)

}
