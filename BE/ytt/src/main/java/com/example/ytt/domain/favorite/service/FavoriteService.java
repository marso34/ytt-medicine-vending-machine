package com.example.ytt.domain.favorite.service;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.favorite.domain.Favorite;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.domain.favorite.repository.FavoriteRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.error.code.ExceptionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.ytt.domain.user.domain.QUser.user;
import static com.example.ytt.domain.favorite.domain.QFavorite.favorite;
import static com.example.ytt.domain.vendingmachine.domain.QVendingMachine.vendingMachine;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final UserRepository userRepository;

    private final JPAQueryFactory jpaQueryFactory;

    public List<VendingMachineDto> getFavorites(Long userId) {
        List<VendingMachine> list = getFavoriteVendingMachines(userId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public boolean isFavorite(Long userId, Long vendingMachineId) {
        return favoriteRepository.existsByUserIdAndVendingMachineId(userId, vendingMachineId);
    }

    public void addFavorite(Long userId, Long vendingMachineId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));
        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        favoriteRepository.save(Favorite.of(user, vendingMachine));
    }

    public boolean removeFavorite(Long userId, Long vendingMachineId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));
        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        favoriteRepository.deleteByUserIdAndVendingMachineId(user.getId(), vendingMachine.getId());

        return true;
    }

    private List<VendingMachine> getFavoriteVendingMachines(Long userId) {
        return jpaQueryFactory
                .selectFrom(vendingMachine)
                .join(favorite).on(vendingMachine.id.eq(favorite.vendingMachine.id))
                .join(favorite.user, user)
                .where(favorite.user.id.eq(userId))
                .fetch();
    }

}
