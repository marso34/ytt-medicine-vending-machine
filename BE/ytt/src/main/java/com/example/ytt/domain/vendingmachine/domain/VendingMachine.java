package com.example.ytt.domain.vendingmachine.domain;

import com.example.ytt.domain.model.Address;
import com.example.ytt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "vending_machine")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"name", "address", "state", "capacity"})
public class VendingMachine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "addressDetails", column = @Column(name = "address", nullable = false))
    @AttributeOverride(name = "location", column = @Column(name = "location", nullable = false))
    private Address address;

    @Enumerated(EnumType.STRING)
    private MachineState state;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Builder
    public VendingMachine(final String name, final Address address, final MachineState state, final Integer capacity) {
        Assert.hasText(name, "자판기 이름은 필수입니다.");
        Assert.notNull(address, "자판기 주소는 필수입니다.");
        Assert.notNull(state, "자판기 상태는 필수입니다.");
        Assert.isTrue(capacity > 0, "자판기 용량은 0보다 커야합니다.");

        this.name = name;
        this.address = address;
        this.state = state;
        this.capacity = capacity;
    }

    public void updateName(final String name) {
        this.name = name;
    }

    public void updateAddress(final Address address) {
        this.address = address;
    }

    public void updateState(final MachineState state) {
        this.state = state;
    }

    public void updateCapacity(final int capacity) {
        this.capacity = capacity;
    }
}
