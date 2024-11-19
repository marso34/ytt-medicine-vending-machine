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
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
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

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public List<VendingMachineDto> getVendingMachinesByName(String name) {
        List<VendingMachine> list = vendingMachineRepository.findByNameContaining(name);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
    public List<VendingMachineDto> getVendingMachinesNearByLocation(Double latitude, Double longitude, Double distance) {
        List<VendingMachine> list = vendingMachineRepository.findNearByLocation(GeometryUtil.createPoint(latitude, longitude), distance);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    /**
     * @deprecated 추후 QueryDSL로 구현한 코드로 대체 예정
     */
    @Deprecated(since="", forRemoval=true)
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

    /* -- QueryDSL을 사용한 코드 -- */

    public List<VendingMachineDto> getVendingMachines(double latitude, double longitude, double distance, String name) {
        List<VendingMachine> list = vendingMachineRepository.getVendingMachines(GeometryUtil.createPoint(latitude, longitude), distance, name);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<VendingMachineDto> getVendingMachinesByMedicine(Double latitude, Double longitude, double distance, Long medicineId) {
        Point point = (latitude == null || longitude == null) ?  null : GeometryUtil.createPoint(latitude, longitude);

        List<VendingMachine> list = vendingMachineRepository.getVendingMachinesByMedicine(point, distance, medicineId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public List<VendingMachineDto> getFavoriteVendingMachines(Long userId) {
        List<VendingMachine> list = vendingMachineRepository.getFavoriteVendingMachines(userId);

        if (list.isEmpty()) {
            throw new VendingMachineException(ExceptionType.NO_CONTENT_VENDING_MACHINE);
        }

        return list.stream().map(VendingMachineDto::from).toList();
    }

    public VendingMachineDetailDto getVendingMachineDetail(Long machineId, Long userId) {
        VendingMachine vendingMachine = vendingMachineRepository.getVendingMachineDetail(machineId).orElseThrow(() -> new VendingMachineException(ExceptionType.NOT_FOUND_VENDING_MACHINE));

        return convertToVendingMachineDetailDto(vendingMachine, userId);
    }


    // 즐겨찾기 여부를 포함한 자판기 상세 정보로 변환
    private VendingMachineDetailDto convertToVendingMachineDetailDto(VendingMachine vendingMachine, Long userId) {
        boolean isFavorite = favoriteRepository.existsByUserIdAndVendingMachineId(userId, vendingMachine.getId());

        return VendingMachineDetailDto.of(vendingMachine, isFavorite);
    }

}
