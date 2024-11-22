package com.example.ytt.domain.inventory.service;

import com.example.ytt.domain.inventory.domain.InboundLog;
import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.dto.InboundLogDto;
import com.example.ytt.domain.inventory.dto.InboundReqDto;
import com.example.ytt.domain.inventory.exception.InboundException;
import com.example.ytt.domain.inventory.repository.InboundLogRepository;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.dto.MedicineDto;
import com.example.ytt.global.error.code.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InboundService {

    private final InventoryRepository inventoryRepository;
    private final InboundLogRepository inboundLogRepository;

    // 약품 입고
    public MedicineDto inboundMedicine(Long machineId, InboundReqDto inboundReqDto, Long userId) {
        Inventory inventory = inventoryRepository
                .getInventory(inboundReqDto.medicineId(), machineId)
                .orElseThrow(() -> new InboundException(ExceptionType.UNREGISTERED_INBOUND)); // 등록되지 않은 약의 입고는 불가. 자판기-약 등록과 입고를 분리하기 위함

        inventoryRepository.save(inventory.addQuantity(inboundReqDto.quantity())); // 수량 추가
        inboundLogRepository.save(InboundLog.of(inventory.getVendingMachine(), inventory.getMedicine(), inboundReqDto.quantity())); // 입고 기록

        return MedicineDto.from(inventory);
    }

    public List<InboundLogDto> getInboundLogs(Long machineId, Long medicineId, String productCode, LocalDateTime startDate, LocalDateTime endDate) {
        List<InboundLog> list = inboundLogRepository.getInboundLogs(machineId, medicineId, productCode, startDate, endDate);

        if (list.isEmpty()) {
            throw new InboundException(ExceptionType.NO_CONTENT_INBOUND_LOG);
        }

        return list.stream().map(InboundLogDto::from).toList();
    }

}