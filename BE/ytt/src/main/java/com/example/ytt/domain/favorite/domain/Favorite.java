package com.example.ytt.domain.favorite.domain;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Entity
@Table(name = "favorite")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(of = {"id", "user", "vendingMachine"})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vending_machine_id", nullable = false)
    private VendingMachine vendingMachine;

    @Builder
    public Favorite(User user, VendingMachine vendingMachine) {
        Assert.notNull(user, "유저는 필수입니다.");
        Assert.notNull(vendingMachine, "자판기는 필수입니다.");

        this.user = user;
        this.vendingMachine = vendingMachine;
    }

    public static Favorite of(User user, VendingMachine vendingMachine) {
        return Favorite.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .build();
    }

}
