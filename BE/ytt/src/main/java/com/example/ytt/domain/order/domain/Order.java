package com.example.ytt.domain.order.domain;

import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "user", "vendingMachine"})
@ToString(of = {"id", "user", "vendingMachine", "state", "orderAt", "completedAt", "price"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderState orderState = OrderState.PENDING;

    @Column(name = "order_at", nullable = false)
    private LocalDateTime orderAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "price", nullable = false)
    private int price;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Column(name = "order_items")
    private List<OrderDetail> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, VendingMachine vendingMachine, int price, List<OrderDetail> orderItems) {
        Assert.notNull(user, "사용자는 필수입니다.");
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");
        Assert.isTrue(price >= 0, "가격은 0 이상이어야 합니다.");
        Assert.notEmpty(orderItems, "주문 항목은 1 이상이어야 합니다.");

        this.user = user;
        this.vendingMachine = vendingMachine;
        this.orderState = OrderState.PENDING;
        this.orderAt = LocalDateTime.now();
        this.completedAt = null;
        this.price = calculateTotalPrice();
        orderItems.forEach(this::addOrderDetail);
    }

    public static Order from(final OrderReqDto reqDto) {
        return Order.builder()
                .user(reqDto.userId())
                .vendingMachine(reqDto.bendingMachineId())
                .price(reqDto.price())
                .build();
    }

    public void addOrderDetail(OrderDetail orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    private int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderDetail::calculateTotalPrice)
                .sum();
    }

    // TODO: 재고 감소 추가
    public void completeOrder() {
        this.orderState = OrderState.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    // TODO: 재고 증가 추가
    public void cancelOrder() {
        this.orderState = OrderState.CANCLED;
    }
}

