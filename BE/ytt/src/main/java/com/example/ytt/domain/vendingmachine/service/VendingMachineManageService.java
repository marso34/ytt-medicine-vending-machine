package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.exception.MedicineException;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineReqDto;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.error.code.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class VendingMachineManageService {

    private final VendingMachineRepository vendingMachineRepository;
    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public VendingMachineDetailDto createVendingMachine(VendingMachineReqDto reqDto) {
        VendingMachine saved = vendingMachineRepository.save(VendingMachine.from(reqDto));

        return VendingMachineDetailDto.from(saved);
    }

    // 자판기 운영 상태로 변경
    public VendingMachineDetailDto openVendingMachine(Long vendingMachineId) {
        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));
        vendingMachine.updateState(MachineState.OPERATING);

        VendingMachine saved = vendingMachineRepository.save(vendingMachine);

        return VendingMachineDetailDto.from(saved);
    }

    // 자판기에 약 등록
    public VendingMachineDetailDto addMedicineToVendingMachine(InboundReqDto reqDto) {
        VendingMachine vendingMachine = vendingMachineRepository.findById(reqDto.machineId()).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));
        Medicine medicine = medicineRepository.findById(reqDto.medicineId()).orElseThrow(() -> new MedicineException(ExceptionType.NOT_FOUND_MEDICINE));
        Inventory inventory = inventoryRepository.save(Inventory.of(vendingMachine, medicine, reqDto.quantity()));

        vendingMachine.addInventory(inventory);

        return VendingMachineDetailDto.from(vendingMachine);
    }

    // 자판기에서 약 삭제
    public void deleteMedicineFromVendingMachine(Long machineId, Long medicineId) {
        inventoryRepository.deleteByVendingMachineIdAndMedicineId(machineId, medicineId);
    }

    // 자판기 삭제
    public void deleteVendingMachine(Long vendingMachineId) {
        vendingMachineRepository.deleteById(vendingMachineId);
    }
}
