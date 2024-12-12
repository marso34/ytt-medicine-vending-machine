package com.example.ytt.domain.management.repository;

import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.ytt.domain.management.domain.QManagement.management;
import static com.example.ytt.domain.order.domain.QOrder.order;
import static com.example.ytt.domain.user.domain.QUser.user;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class ManagementRepositoryImpl implements ManagementRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<VendingMachine> getManagedVendingMachines(Long userId) {
        return jpaQueryFactory
                .selectFrom(vendingMachine)
                .join(management).on(vendingMachine.id.eq(management.vendingMachine.id))
                .join(management.user, user)
                .where(management.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<User> getManagers(Long vendingMachineId) {
        return jpaQueryFactory
                .selectFrom(user)
                .join(user.managements, management)
                .join(management.vendingMachine, vendingMachine)
                .where(vendingMachine.id.eq(vendingMachineId))
                .fetch();
    }

    @Override
    public boolean isMachineManager(Long vendingMachineId, Long userId) {
        Long cnt = jpaQueryFactory
                .select(user.count())
                .from(user)
                .join(user.managements, management)
                .join(management.vendingMachine, vendingMachine)
                .where(vendingMachine.id.eq(vendingMachineId),
                        user.id.eq(userId))
                .fetchOne();

        return cnt != null && cnt > 0;
    }

}
