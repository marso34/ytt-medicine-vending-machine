package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.repository.FavoriteRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VendingMachineFindService {

    private final VendingMachineRepository vendingMachineRepository;
    private final InventoryRepository inventoryRepository;
    private final FavoriteRepository favoriteRepository;

    // TODO: 자판기가 없을 때 예외 처리도 고려해야 함

    public List<VendingMachineDto> getAllVendingMachines() {
        return vendingMachineRepository.findAll()
                .stream()
                .map(VendingMachineDto::from)
                .toList();
    }

    public List<VendingMachineDto> getVendingMachinesByName(String name) {
        return vendingMachineRepository.findByNameContaining(name)
                .stream()
                .map(VendingMachineDto::from)
                .toList();
    }

    public List<VendingMachineDto> getVendingMachinesNearByLocation(Double latitude, Double longitude, Double distance) {
        return vendingMachineRepository.findNearByLocation(GeometryUtil.createPoint(latitude, longitude), distance)
                .stream()
                .map(VendingMachineDto::from)
                .toList();
    }

    public List<VendingMachineDto> getVendingMachinesByMedicine(Long medicineId) {
        return inventoryRepository.findByMedicineId(medicineId)
                .stream()
                .map(inventory -> VendingMachineDto.from(inventory.getVendingMachine()))
                .toList();
    }

    public VendingMachineDetailDto getVendingMachineDetail(Long machineId, Long userId) { // Long id -> Long id, Long userId
        VendingMachine vendingMachine = vendingMachineRepository.findById(machineId).orElseThrow();

        return convertToVendingMachineDetailDto(vendingMachine, userId);
    }

    public VendingMachineDetailDto convertToVendingMachineDetailDto(VendingMachine vendingMachine, Long userId) { // VendingMachine vendingMachine -> VendingMachine vendingMachine, Long userId
        boolean isFavorite = favoriteRepository.existsByUserIdAndVendingMachineId(userId, vendingMachine.getId());

        return VendingMachineDetailDto.of(vendingMachine, isFavorite);
    }

}
