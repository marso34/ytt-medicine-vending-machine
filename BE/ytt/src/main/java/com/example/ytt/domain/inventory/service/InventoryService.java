package com.example.ytt.domain.inventory.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // 자판기의 재고 목록 조회
    public List<MedicineDetailDto> getMedicinesByVendingMachine(Long machineId) {
        return inventoryRepository.findByVendingMachineId(machineId)
                .stream()
                .map(MedicineDetailDto::from)
                .toList();
    }

    // 특정 자판기의 특정 약품 조회
    public MedicineDetailDto getMedicineByInventory(Long machineId, Long medicineId) {
        Inventory inventory = inventoryRepository.findByMedicineIdAndVendingMachineId(machineId, medicineId).orElseThrow();

        return MedicineDetailDto.from(inventory);
    }

}
