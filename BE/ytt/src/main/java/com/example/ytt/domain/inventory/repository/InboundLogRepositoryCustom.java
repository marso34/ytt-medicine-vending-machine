package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.InboundLog;

import java.time.LocalDateTime;
import java.util.List;

public interface InboundLogRepositoryCustom {

    List<InboundLog> getInboundLogs(Long machineId, Long medicineId, String productCode, LocalDateTime startDate, LocalDateTime endDate);

}


