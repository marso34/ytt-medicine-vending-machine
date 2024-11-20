package com.example.ytt.domain.management.domain;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "management")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Management extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "management_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @Builder
    public Management(User user, VendingMachine vendingMachine) {
        Assert.notNull(user, "유저는 필수입니다.");
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");

        this.user = user;
        this.vendingMachine = vendingMachine;
    }

    public static Management of(User user, VendingMachine vendingMachine) {
        return Management.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .build();
    }

}
