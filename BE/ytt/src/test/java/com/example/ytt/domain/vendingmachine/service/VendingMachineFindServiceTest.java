package com.example.ytt.domain.vendingmachine.service;

import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDetailDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import com.example.ytt.global.util.GeometryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VendingMachineFindServiceTest {

    @InjectMocks
    VendingMachineFindService vendingMachineFindService;

    @Mock
    VendingMachineRepository vendingMachineRepository;
    List<VendingMachine> vendingMachines;

    @BeforeEach
    void setUp() {
        vendingMachines = List.of(
                createVendingMachine("강릉원주대 자판기1", Address.from("address1", 37.305121, 127.922653)),
                createVendingMachine("강릉원주대 자판기2", Address.from("address2", 37.307899, 127.920770)),
                createVendingMachine("강릉원주대 자판기3", Address.from("address3", 37.311945, 127.927402)),
                createVendingMachine("강릉원주대 자판기4", Address.from("address4", 37.319237, 127.935006))
        );
    }

    @DisplayName("모든 자판기 조회 테스트")
    @Test
    void getAllVendingMachines() {
        // given
        given(vendingMachineRepository.findAll()).willReturn(vendingMachines);

        // when
        List<VendingMachineDto> vendingMachineDtos = vendingMachineFindService.getAllVendingMachines();

        // then
        assertThat(vendingMachineDtos)
                .isNotEmpty()
                .isEqualTo(vendingMachines.stream().map(VendingMachineDto::from).toList());
    }

    @DisplayName("자판기 이름으로 조회 테스트")
    @Test
    void getVendingMachinesByName() {
        // given
        List<VendingMachine> filteredVendingMachine = vendingMachines.stream().filter(v -> v.getName().contains("강릉원주대")).toList();

        given(vendingMachineRepository.findByNameContaining("강릉원주대")).willReturn(filteredVendingMachine);

        // when
        List<VendingMachineDto> vendingMachineDtos = vendingMachineFindService.getVendingMachinesByName("강릉원주대");

        // then
        assertThat(vendingMachineDtos)
                .isNotEmpty()
                .isEqualTo(filteredVendingMachine.stream().map(VendingMachineDto::from).toList());
    }

    @DisplayName("주변 위치 자판기 조회 테스트")
    @Test
    void getVendingMachinesNearByLocation() {
        // given
        given(vendingMachineRepository.findNearByLocation(GeometryUtil.createPoint(37.305121, 127.922653), 25000.0)).willReturn(vendingMachines);

        // when
        List<VendingMachineDto> vendingMachineDtos = vendingMachineFindService.getVendingMachinesNearByLocation(37.305121, 127.922653, 25000.0);

        // then
        assertThat(vendingMachineDtos)
                .isNotEmpty()
                .isEqualTo(vendingMachines.stream().map(VendingMachineDto::from).toList());
    }

    @DisplayName("자판기 상세 조회 테스트")
    @Test
    void getVendingMachineDetail() {
        // given
        VendingMachine vendingMachine = vendingMachines.get(0);

        given(vendingMachineRepository.findById(1L)).willReturn(java.util.Optional.of(vendingMachine));

        // when
        VendingMachineDetailDto vendingMachineDetailDto = vendingMachineFindService.getVendingMachineDetail(1L);

        // then
        assertThat(vendingMachineDetailDto)
                .isNotNull()
                .isEqualTo(VendingMachineDetailDto.from(vendingMachine, false));
    }

    // vendingMachine 생성 메서드
    private VendingMachine createVendingMachine(String name, Address address) {
        return VendingMachine.builder()
                .name(name)
                .address(address)
                .state(MachineState.OPERATING)
                .capacity(10)
                .build();
    }

}