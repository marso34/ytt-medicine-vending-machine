package com.example.ytt.domain.favorite.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.ytt.domain.favorite.domain.QFavorite.favorite;
import static com.example.ytt.domain.user.domain.QUser.user;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<VendingMachine> getFavoriteVendingMachines(Long userId) {
        return jpaQueryFactory
                .selectFrom(vendingMachine)
                .join(favorite).on(vendingMachine.id.eq(favorite.vendingMachine.id))
                .join(favorite.user, user)
                .where(favorite.user.id.eq(userId))
                .fetch();
    }
}
