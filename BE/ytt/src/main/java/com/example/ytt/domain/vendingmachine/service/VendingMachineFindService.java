package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.repository.InventoryRepository;
import com.example.ytt.domain.medicine.exception.MedicineException;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.exception.VendingMachineException;
import com.example.ytt.domain.vendingmachine.repository.FavoriteRepository;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.error.ExceptionType;
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
    private final MedicineRepository medicineRepository;

    public List<VendingMachineDto> getAllVendingMachines() {
        List<VendingMachine> list = vendingMachineRepository.findAll();

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<VendingMachineDto> getVendingMachinesByName(String name) {
        List<VendingMachine> list = vendingMachineRepository.findByNameContaining(name);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<VendingMachineDto> getVendingMachinesNearByLocation(Double latitude, Double longitude, Double distance) {
        List<VendingMachine> list = vendingMachineRepository.findNearByLocation(GeometryUtil.createPoint(latitude, longitude), distance);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<VendingMachineDto> getVendingMachinesByMedicine(Long medicineId) {
        if (!medicineRepository.existsById(medicineId)) {
            throw new MedicineException(ExceptionType.NOT_FOUND_MEDICINE); // 존재하는 약인지 검증
        }

        List<Inventory> list = inventoryRepository.findByMedicineId(medicineId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_MEDICINE_REGISTER); // 약 등록 정보가 있는지 검증
        }

        return list.stream().map(inventory -> VendingMachineDto.from(inventory.getVendingMachine())).toList(); // distinct() 추가, 기본적으로 중복이 없지만 명시적으로 추가할지 고민
    }

    public VendingMachineDetailDto getVendingMachineDetail(Long machineId, Long userId) {
        VendingMachine vendingMachine = vendingMachineRepository.findById(machineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        return convertToVendingMachineDetailDto(vendingMachine, userId);
    }

    private VendingMachineDetailDto convertToVendingMachineDetailDto(VendingMachine vendingMachine, Long userId) {
        boolean isFavorite = favoriteRepository.existsByUserIdAndVendingMachineId(userId, vendingMachine.getId());

        return VendingMachineDetailDto.of(vendingMachine, isFavorite);
    }

}
