package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.ytt.domain.inventory.domain.QInventory.inventory;
import static com.example.ytt.domain.medicine.domain.QIngredient.ingredient;
import static com.example.ytt.domain.medicine.domain.QMedicine.medicine;
import static com.example.ytt.domain.medicine.domain.QMedicineIngredient.medicineIngredient;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Inventory> getInventories(Long vendingMachineId) {
        return jpaQueryFactory
                .selectFrom(inventory)
                .join(inventory.vendingMachine, vendingMachine)
                .join(inventory.medicine, medicine).fetchJoin()
                .where(
                        equalsVendingMachineId(vendingMachineId)
                ).fetch();
    }

    @Override
    public List<Inventory> getInventories(Long vendingMachineId, List<String> productCodes) {
        return jpaQueryFactory
                .selectFrom(inventory)
                .join(inventory.vendingMachine, vendingMachine)
                .join(inventory.medicine, medicine).fetchJoin()
                .where(
                        equalsVendingMachineId(vendingMachineId),
                        inProductCode(productCodes)
                )
                .orderBy(orderByFieldList(productCodes)) // poductCodes 순서대로 정렬
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 비관적 락 설정
                .fetch();
    }

    @Override
    public Optional<Inventory> getInventory(Long vendingMachineId, Long medicineId) {
        Inventory inventory1 = jpaQueryFactory
                .selectFrom(inventory)
                .join(inventory.vendingMachine, vendingMachine)
                .join(inventory.medicine, medicine).fetchJoin()
                .join(medicine.ingredients, medicineIngredient).fetchJoin()
                .join(medicineIngredient.ingredient, ingredient).fetchJoin()
                .where(
                        equalsVendingMachineId(vendingMachineId),
                        equalsMedicineIdEq(medicineId)
                ).fetchOne();

        return Optional.ofNullable(inventory1);
    }


    // 자판기 ID 필터
    private BooleanExpression equalsVendingMachineId(Long vendingMachineId) {
        return vendingMachineId != null ? vendingMachine.id.eq(vendingMachineId) : null;
    }

    // 약품 ID 필터
    private BooleanExpression equalsMedicineIdEq(Long medicineId) {
        return medicineId != null ? medicine.id.eq(medicineId) : null;
    }

    private BooleanExpression inProductCode(List<String> productCodes) {
        return productCodes != null ? medicine.productCode.in(productCodes) : null;
    }

    private OrderSpecifier<?> orderByFieldList(List<String> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            return null;
        }

        StringBuilder template = new StringBuilder("FIELD({0}");

        List<Object> list = new ArrayList<>();
        list.add(medicine.productCode);

        for (int i = 0; i < productCodes.size(); i++) {
            template.append(", {").append(i + 1).append("}");
            list.add(productCodes.get(i));
        }

        template.append(")");

        return Expressions.stringTemplate(template.toString(), list).asc();
    }

    private OrderSpecifier<?> orderByFieldListForH2(List<String> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            return null;
        }

        CaseBuilder.Cases<Integer, NumberExpression<Integer>> caseBuilder = new CaseBuilder().when(medicine.productCode.eq(productCodes.get(0))).then(1);

        for (int i = 1; i < productCodes.size(); i++) {
            caseBuilder.when(medicine.productCode.eq(productCodes.get(i))).then(i+1);
        }

        NumberExpression<Integer> caseOrder = caseBuilder.otherwise(productCodes.size() + 1);

        return caseOrder.asc();
    }

}
