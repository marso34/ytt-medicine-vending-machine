package com.example.ytt.domain.vendingmachine.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.ytt.domain.inventory.domain.QInventory.inventory;
import static com.example.ytt.domain.medicine.domain.QMedicine.medicine;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class VendingMachineRepositoryImpl implements VendingMachineRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<VendingMachine> getVendingMachines(Point location, double distance, String name) {
        return jpaQueryFactory
                .selectFrom(vendingMachine)
                .leftJoin(vendingMachine.inventories, inventory)
                .leftJoin(inventory.medicine, medicine)
                .where(
                        distanceLessThan(location, distance),
                        nameContains(name)
                )
                .fetch();
    }

    @Override
    public List<VendingMachine> getVendingMachinesByMedicine(Point location, double distance, Long medicineId) {
        return jpaQueryFactory
                .selectFrom(vendingMachine)
                .leftJoin(vendingMachine.inventories, inventory)
                .leftJoin(inventory.medicine, medicine)
                .where(
                        distanceLessThan(location, distance),
                        medicineContains(medicineId)
                )
                .fetch();
    }

    // 자판기 이름 필터
    private BooleanExpression nameContains(String name) {
        return name == null ? null : vendingMachine.name.contains(name);
    }

    // 자판기 검색 범위 필터
    private BooleanExpression distanceLessThan(Point location, Double distance) {
        if (location == null || distance == null) {
            return null;
        }

        // 공간 거리 표현식 정의
        NumberTemplate<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "ST_DISTANCE(ST_TRANSFORM({0}, 3857), ST_TRANSFORM({1}, 3857))",
                vendingMachine.address.location, location);

        return distanceExpression.loe(distance);
    }

    // 특정 약을 포함하는 자판기 리스트 필터
    private BooleanExpression medicineContains(Long medicineId) {
        return medicineId == null ? null : inventory.medicine.id.eq(medicineId);
    }

}
