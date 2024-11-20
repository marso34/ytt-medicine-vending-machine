package com.example.ytt.domain.management.repository;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;

import java.util.List;

public interface ManagementRepositoryCustom {

    List<VendingMachine> getManagedVendingMachines(Long userId);

    List<User> getManagers(Long vendingMachineId);

}
