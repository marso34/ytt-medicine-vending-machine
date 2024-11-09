    package com.example.ytt.domain.order.domain;

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
    @Table(name = "orders")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode(of = {"id", "user", "vendingMachine"})
    @ToString(of = {"id", "user", "vendingMachine", "orderState", "orderAt", "completedAt", "totalPrice"})
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "uuid", nullable = false)
        private Long id;

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
        @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private List<OrderDetail> orderItems = new ArrayList<>();

        @Builder
        public Order(User user, VendingMachine vendingMachine, List<OrderDetail> orderItems) {
            Assert.notNull(user, "사용자는 필수입니다.");
            Assert.notNull(vendingMachine, "자판기는 필수입니다.");
            Assert.notEmpty(orderItems, "주문 항목은 1 이상이어야 합니다.");

            this.user = user;
            this.vendingMachine = vendingMachine;
            this.orderState = OrderState.PENDING;
            this.orderAt = LocalDateTime.now();
            this.completedAt = null;
            orderItems.forEach(this::addOrderDetail);
            this.totalPrice = calculateTotalPrice();
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

        public void completeOrder() {
            this.orderState = OrderState.COMPLETED;
            this.completedAt = LocalDateTime.now();

            for (OrderDetail orderItem : orderItems) {
                VendingMachine vendingMachine = this.vendingMachine;

                vendingMachine.getInventories().stream()
                        .filter(inventory -> inventory.getMedicine().equals(orderItem.getMedicine()))
                        .findFirst()
                        .ifPresent(inventory -> inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity()));
            }
        }

        public void cancelOrder() {
            if(this.getOrderState() == OrderState.COMPLETED){
                throw new IllegalStateException("이미 주문완료된 상품은 취소가 불가능합니다.");
            }
            this.orderState = OrderState.CANCELED;
            for (OrderDetail orderItem : orderItems) {
                VendingMachine vendingMachine = this.vendingMachine;

                vendingMachine.getInventories().stream()
                        .filter(inventory -> inventory.getMedicine().equals(orderItem.getMedicine()))
                        .findFirst()
                        .ifPresent(inventory -> inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity()));

            }
        }
    }

