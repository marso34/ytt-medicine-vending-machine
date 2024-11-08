package com.example.ytt.domain.inventory.domain;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "inbound_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InboundLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_log_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; // created_by도 고려할것

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Builder
    public InboundLog(final VendingMachine vendingMachine, final Medicine medicine, final int quantity) {
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.isTrue(quantity > 0, "수량은 0보다 커야합니다.");

        this.vendingMachine = vendingMachine;
        this.medicine = medicine;
        this.quantity = quantity;
    }

    public static InboundLog of(final VendingMachine vendingMachine, final Medicine medicine, final int quantity) {
        return InboundLog.builder()
                .vendingMachine(vendingMachine)
                .medicine(medicine)
                .quantity(quantity)
                .build();
    }

}