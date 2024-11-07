package com.example.ytt.domain.inventory.service;

import com.example.ytt.domain.inventory.domain.InboundLog;
import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.dto.InboundLogDto;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.repository.InboundLogRepository;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InboundService {

    private final InventoryRepository inventoryRepository;
    private final InboundLogRepository inboundLogRepository;

    private final VendingMachineRepository vendingMachineRepository;
    private final MedicineRepository medicineRepository;

    // 약품 입고
    public MedicineDto inboundMedicine(InboundReqDto inboundReqDto) {
        Inventory inventory = inventoryRepository
                .findByMedicineIdAndVendingMachineId(inboundReqDto.medicineId(), inboundReqDto.machineId())
                .orElseThrow(() -> new IllegalArgumentException("기존에 없던 약의 재고 변경은 불가능합니다.")); // 자판기-약 등록과 입고를 분리하기 위함

        inventoryRepository.save(inventory.addQuantity(inboundReqDto.quantity())); // 수량 추가
        inboundLogRepository.save(InboundLog.of(inventory.getVendingMachine(), inventory.getMedicine(), inboundReqDto.quantity())); // 입고 기록

        return MedicineDto.from(inventory);
    }

    // 입고 기록 조회 (모든 약)
    public List<InboundLogDto> getInboundAllLogs(Long machineId) {
        return inboundLogRepository.findByVendingMachineId(machineId)
                .stream()
                .map(InboundLogDto::from)
                .toList();
    }

    // 입고 기록 조회 (특정 약)
    public List<InboundLogDto> getInboundLogs(Long machineId, Long medicineId) {
        return inboundLogRepository.findByVendingMachineIdAndMedicineId(machineId, medicineId)
                .stream()
                .map(InboundLogDto::from)
                .toList();
    }
}