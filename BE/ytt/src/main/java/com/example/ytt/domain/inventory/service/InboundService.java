package com.example.ytt.domain.inventory.service;

import com.example.ytt.domain.inventory.domain.InboundLog;
import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.dto.InboundLogDto;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.repository.InboundLogRepository;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InboundService {

    private final InventoryRepository inventoryRepository;
    private final InboundLogRepository inboundLogRepository;

    private final VendingMachineRepository vendingMachineRepository;
    private final MedicineRepository medicineRepository;

    // 약품 입고
    public int inboundMedicine(InboundReqDto inboundReqDto) {
        Optional<Inventory> inventory = inventoryRepository.findByMedicineIdAndVendingMachineId(inboundReqDto.medicineId(), inboundReqDto.machineId());

        inventory.ifPresentOrElse(
                // 기존에 등록되어 있는 약만 수량 추가
                inven -> {
                    inventoryRepository.save(inven.addQuantity(inboundReqDto.quantity())); // 수량 추가
                    inboundLogRepository.save(InboundLog.of(inven.getVendingMachine(), inven.getMedicine(), inboundReqDto.quantity())); // 입고 기록
                },
                // 기존에 등록되어 있는 약이 아니면 예외 처리 (재고 추가와 약 등록을 별도로 관리하기 위함)
                () -> { throw new IllegalArgumentException("기존에 없던 약의 재고 변경은 불가능합니다."); }
        );

//        return inventory.get().getQuantity() + inboundReqDto.quantity();
        return inboundReqDto.quantity();
    }

    // 입고 기록 조회
    public List<InboundLogDto> getInboundLogsByVendingMachine(Long machineId) {
        return inboundLogRepository.findByVendingMachineId(machineId)
                .stream()
                .map(InboundLogDto::from)
                .toList();
    }
}