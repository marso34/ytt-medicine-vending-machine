package com.example.ytt.domain.order.domain;

import com.example.ytt.domain.medicine.domain.Medicine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "order_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "order", "medicine"})
@ToString(of = {"id", "order", "medicine", "quantity"})
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Transient // DB에 저장하지 않음
    private int totalPrice;

    @Builder
    public OrderDetail(final Order order, final Medicine medicine, final int quantity){
        Assert.notNull(order, "주문은 필수입니다.");
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.isTrue(quantity >= 0, "수량은 0보다 크거나 같아야합니다.");

        this.order = order;
        this.medicine = medicine;
        this.quantity = quantity;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public int calculateTotalPrice() {
        return medicine.getPrice() * quantity;
    }

    public static OrderDetail of(final Order order, final Medicine medicine, int quantity) {
        return OrderDetail.builder()
                .order(order)
                .medicine(medicine)
                .quantity(quantity)
                .build();
    }
}
