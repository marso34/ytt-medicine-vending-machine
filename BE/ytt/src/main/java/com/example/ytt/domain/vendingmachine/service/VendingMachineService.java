package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.TempReqDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineReqDto;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class VendingMachineService {

    private final VendingMachineRepository vendingMachineRepository;
    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public VendingMachineDetailDto createVendingMachine(VendingMachineReqDto reqDto) {
        VendingMachine saved = vendingMachineRepository.save(VendingMachine.from(reqDto));

        return VendingMachineDetailDto.from(saved);
    }

    // 자판기 운영 상태로 변경
    public VendingMachine openVendingMachine(Long vendingMachineId) {
        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow();
        vendingMachine.updateState(MachineState.OPERATING);

        return vendingMachine;
    }

    // 자판기에 약 등록
    public VendingMachineDetailDto addMedicineToVendingMachine(TempReqDto reqDto) {
        VendingMachine vendingMachine = vendingMachineRepository.findById(reqDto.machine_id()).orElseThrow();
        Medicine medicine = medicineRepository.findById(reqDto.medicine_id()).orElseThrow();
        inventoryRepository.save(Inventory.of(vendingMachine, medicine, reqDto.quantity()));

        VendingMachine saved = vendingMachineRepository.findById(reqDto.machine_id()).orElseThrow();

        return VendingMachineDetailDto.from(saved);
    }

    public void deleteVendingMachine(Long vendingMachineId) {
        vendingMachineRepository.deleteById(vendingMachineId);
    }
}
