package com.example.ytt.domain.order.repository;

import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderState;
import com.example.ytt.domain.order.util.UUIDutil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.ytt.domain.medicine.domain.QMedicine.medicine;
import static com.example.ytt.domain.order.domain.QOrder.order;
import static com.example.ytt.domain.order.domain.QOrderItem.orderItem;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByIDAndUserId(String id, Long userId) {
        Long count = jpaQueryFactory
                .select(order.id.count())
                .from(order)
                .where(order.id.eq(UUIDutil.convertUUID(id)),
                        eqUser(userId))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<Order> getOrders(Long userId, Long machineId, OrderState state) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.vendingMachine, vendingMachine).fetchJoin()
                .where(eqUser(userId),
                        eqVendingMachine(machineId),
                        eqState(state))
                .orderBy(order.orderAt.desc())
                .fetch();
    }

    @Override
    public Optional<Order> getOrderDetail(String orderId) {
        Order orders = jpaQueryFactory
                .selectFrom(order)
                .join(order.vendingMachine, vendingMachine).fetchJoin()
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.medicine, medicine).fetchJoin()
                .where(order.id.eq(UUIDutil.convertUUID(orderId)))
                .fetchOne();

        return Optional.ofNullable(orders);
    }

    @Override
    public Long getOrderCount(Long machineId) {
        return jpaQueryFactory
                .select(order.id.count())
                .from(order)
                .where(eqVendingMachine(machineId),
                        order.orderState.eq(OrderState.PENDING).or(order.orderState.eq(OrderState.STORED)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
    }

    private BooleanExpression eqUser(Long userId) {
        return userId != null ? order.user.id.eq(userId) : null;
    }

    private BooleanExpression eqVendingMachine(Long machineId) {
        return machineId != null ? order.vendingMachine.id.eq(machineId) : null;
    }

    private BooleanExpression eqState(OrderState state) {
        return state != null ? order.orderState.eq(state) : null;
    }

}
