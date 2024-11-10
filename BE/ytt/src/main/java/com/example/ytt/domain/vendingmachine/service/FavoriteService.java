package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.vendingmachine.domain.Favorite;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.domain.vendingmachine.repository.FavoriteRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.error.code.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final UserRepository userRepository;

    // 유저의 즐겨찾기 (자판기) 목록 조회
    public List<VendingMachineDto> getFavorites(Long userId) {
        List<Favorite> list = favoriteRepository.findByUserId(userId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream()
                .map(favorite -> VendingMachineDto.from(favorite.getVendingMachine()))
                .toList();
    }

    // 유저의 즐겨찾기 (자판기) 추가
    public Favorite addFavorite(Long userId, Long vendingMachineId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));

        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        return favoriteRepository.save(Favorite.of(user, vendingMachine));
    }

    // 유저의 즐겨찾기 (자판기) 삭제
    public void deleteFavorite(Long userId, Long vendingMachineId) {
        favoriteRepository.deleteByUserIdAndVendingMachineId(userId, vendingMachineId);
    }

}
