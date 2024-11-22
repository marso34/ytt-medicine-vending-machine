package com.example.ytt.domain.inventory.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.exception.InventoryException;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.dto.MedicineDetailDto;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.global.error.code.ExceptionType;
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
    public List<MedicineDto> getMedicinesByVendingMachine(Long machineId) {
        List<Inventory> list = inventoryRepository.getInventories(machineId);

        if (list.isEmpty()) {
            throw new InventoryException(ExceptionType.NO_CONTENT_INVENOTRY);
        }

        return list.stream().map(MedicineDto::from).toList();
    }

    // 특정 자판기의 특정 약품 조회
    public MedicineDetailDto getMedicineByInventory(Long machineId, Long medicineId) {
        Inventory inventory = inventoryRepository.getInventory(machineId, medicineId).orElseThrow(() -> new InventoryException(ExceptionType.NOT_FOUND_INVENOTRY));

        return MedicineDetailDto.from(inventory);
    }

    /* 저장, 삭제 */

    public Inventory save(VendingMachine vendingMachine, Medicine medicine, int quantity) {
        return inventoryRepository.save(Inventory.of(vendingMachine, medicine, quantity));
    }

    public void delete(Long machineId, Long medicineId) {
        inventoryRepository.deleteByVendingMachineIdAndMedicineId(machineId, medicineId);
    }

}
