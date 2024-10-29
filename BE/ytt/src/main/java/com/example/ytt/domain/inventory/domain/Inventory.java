package com.example.ytt.domain.inventory.domain;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id", "vendingMachine", "medicine"})
@ToString(of = {"id", "vendingMachine", "medicine", "quantity"})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Builder
    public Inventory(final VendingMachine vendingMachine, final Medicine medicine, final int quantity) {
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");
        Assert.notNull(medicine, "약은 필수입니다.");
        Assert.isTrue(quantity >= 0, "수량은 0보다 크거나 같아야합니다.");

        this.vendingMachine = vendingMachine;
        this.medicine = medicine;
        this.quantity = quantity;
    }

    public static Inventory of(final VendingMachine vendingMachine, final Medicine medicine) {
        return of(vendingMachine, medicine, 0);
    }

    public static Inventory of(final VendingMachine vendingMachine, final Medicine medicine, final int quantity) {
        return Inventory.builder()
                .vendingMachine(vendingMachine)
                .medicine(medicine)
                .quantity(quantity)
                .build();
    }

    public Inventory setQuantity(int quantity) {
        Assert.isTrue(quantity >= 0, "수량은 0보다 크거나 같아야합니다."); // 예외 처리 나중에
        this.quantity = quantity;

        return this;
    }

}
