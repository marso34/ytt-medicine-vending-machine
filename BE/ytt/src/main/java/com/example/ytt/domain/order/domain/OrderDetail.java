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
    @Column(name = "order_detail_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Transient // DB에 저장하지 않음
    private int totalPrice;

    @Builder
    public OrderDetail(final Medicine medicine, final int quantity){
        Assert.notNull(medicine, "구매할 약은 필수입니다.");
        Assert.isTrue(quantity > 0, "수량은 0개보다 커야 합니다.");

        this.medicine = medicine;
        this.quantity = quantity;
        this.totalPrice = calculateTotalPrice();
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public int calculateTotalPrice() {
        return medicine.getPrice() * quantity;
    }

    public static OrderDetail of(final Medicine medicine, int quantity) {
        return OrderDetail.builder()
                .medicine(medicine)
                .quantity(quantity)
                .build();
    }
}
