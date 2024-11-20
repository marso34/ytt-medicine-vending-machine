package com.example.ytt.domain.favorite.repository;

import com.example.ytt.domain.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndVendingMachineId(Long userId, Long vendingMachineId);

    List<Favorite> findByUserId(Long userId);

    void deleteByUserIdAndVendingMachineId(Long userId, Long vendingMachineId);
}
