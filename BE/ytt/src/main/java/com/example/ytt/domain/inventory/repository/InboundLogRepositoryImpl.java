package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.InboundLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.ytt.domain.inventory.domain.QInboundLog.inboundLog;
import static com.example.ytt.domain.medicine.domain.QMedicine.medicine;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class InboundLogRepositoryImpl implements InboundLogRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<InboundLog> getInboundLogs(Long machineId, Long medicineId, String productCode, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory
                .selectFrom(inboundLog)
                .join(inboundLog.vendingMachine, vendingMachine).fetchJoin()
                .join(inboundLog.medicine, medicine).fetchJoin()
                .where(
                        equalsMachineId(machineId),
                        equalsMedicineId(medicineId),
                        equalsProductCode(productCode),
                        betweenInboundedAt(startDate, endDate))
                .fetch();
    }

    private BooleanExpression equalsMachineId(Long machineId) {
        return machineId != null ? inboundLog.vendingMachine.id.eq(machineId) : null;
    }

    private BooleanExpression equalsMedicineId(Long medicineId) {
        return medicineId != null ? inboundLog.medicine.id.eq(medicineId) : null;
    }

    private BooleanExpression equalsProductCode(String productCode) {
        return productCode != null ? inboundLog.medicine.productCode.eq(productCode) : null;
    }

    private BooleanExpression betweenInboundedAt(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null ? inboundLog.createdAt.between(startDate, endDate) : null;
    }
}
