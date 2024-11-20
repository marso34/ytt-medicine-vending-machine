package com.example.ytt.domain.management.service;

import com.example.ytt.domain.management.domain.Management;
import com.example.ytt.domain.management.repository.ManagementRepository;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.error.code.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementRepository managementRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final UserRepository userRepository;

    /* 조회 */

    public List<VendingMachineDto> getManagedVendingMachines(Long userId) {
        List<VendingMachine> list = managementRepository.getManagedVendingMachines(userId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<UserDto> getManagers(Long vendingMachineId) {
        List<User> list =  managementRepository.getManagers(vendingMachineId);

        if (list.isEmpty()) {
            throw new UserException(ExceptionType.NOT_FOUND_USER);
        }

        return list.stream().map(UserDto::from).toList();
    }

    // TODO: 모든 관리자 조회 기능 구현

    public List<UserDto> getAllManagers() {
        return null;
    }

    /* 생성, 삭제 */

    public boolean isManaged(Long userId, Long vendingMachineId) {
        return managementRepository.existsByUserIdAndVendingMachineId(userId, vendingMachineId);
    }

    public void addManager(User user, VendingMachine vendingMachine) {
        managementRepository.save(Management.of(user, vendingMachine));
    }

    public void addManagerById(Long userId, Long vendingMachineId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));
        VendingMachine vendingMachine = vendingMachineRepository.findById(vendingMachineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        addManager(user, vendingMachine);
    }

    public void removeManager(Long userId, Long vendingMachineId) {
        managementRepository.deleteByUserIdAndVendingMachineId(userId, vendingMachineId);
    }

}
