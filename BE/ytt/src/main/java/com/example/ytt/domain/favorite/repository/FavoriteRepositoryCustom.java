package com.example.ytt.domain.favorite.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;

import java.util.List;

public interface FavoriteRepositoryCustom {

    List<VendingMachine> getFavoriteVendingMachines(Long userId);

}
