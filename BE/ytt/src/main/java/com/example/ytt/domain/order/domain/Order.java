package com.example.ytt.domain.order.domain;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "user", "vendingMachine"})
@ToString(of = {"id", "user", "vendingMachine", "orderState", "orderAt", "completedAt", "totalPrice"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private OrderState orderState = OrderState.PENDING;

    @Column(name = "order_at", nullable = false)
    private LocalDateTime orderAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Setter
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, VendingMachine vendingMachine, OrderState state, List<OrderItem> orderItems) {
        Assert.notNull(user, "사용자는 필수입니다.");
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");
        Assert.notNull(state, "주문 상태는 필수입니다.");
        Assert.notEmpty(orderItems, "주문 항목은 1 이상이어야 합니다.");

        this.user = user;
        this.vendingMachine = vendingMachine;
        this.orderState = state;
        this.totalPrice = 0;

        this.orderAt = LocalDateTime.now();
        this.completedAt = null;

        orderItems.forEach(this::addOrderDetail);
    }

    public void addOrderDetail(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);

        this.totalPrice += orderItem.getTotalPrice();
    }

    public Order setOrderState(OrderState orderState) {
        this.orderState = orderState;

        return this;
    }

    public void setCompletedAt() {
        this.completedAt = LocalDateTime.now();
    }

}
