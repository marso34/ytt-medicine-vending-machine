package com.example.ytt.domain.order.service;

import com.example.ytt.domain.inventory.domain.Inventory;
import com.example.ytt.domain.inventory.service.InventoryService;
import com.example.ytt.domain.order.dto.request.OrderItemReqDto;
import com.example.ytt.domain.order.dto.request.OrderReqDto;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class OrderManageServiceTest {

    @Autowired
    OrderManageService orderManageService;

    @Autowired
    private VendingMachineRepository vendingMachineRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        user = createUser("example@naver.com", "홍길동");
        user = userRepository.save(user);

        vendingMachine = vendingMachineRepository.getVendingMachineDetail(1L).orElse(null);
    }

    @Test
    @DisplayName("주문 요청 테스트")
    void 주문_생성() throws InterruptedException {
        List<Inventory> inventories = vendingMachine.getInventories();
        List<String> productCodes = inventories.stream()
                .map(inventory -> inventory.getMedicine().getProductCode())
                .toList();

        List<OrderItemReqDto> orderItems = productCodes.stream()
                .map(productCode -> new OrderItemReqDto(productCode, 1))
                .toList();
        OrderReqDto order = new OrderReqDto(user.getId(), vendingMachine.getId(), orderItems);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    // 테스트 코드 실행 전 InventoryRepositoryImpl에서 orderBy 메서드 교체할 것
                    orderManageService.createOrder(order);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        List<Inventory> inventoriesAfterOrder = inventoryService.getInventories(vendingMachine.getId(), productCodes);

        inventoriesAfterOrder.forEach(inventory -> {
            System.out.println(inventory.getMedicine().getName() + " : " + inventory.getQuantity());

            assertThat(inventory.getQuantity()).isEqualTo(100 - threadCount);
        });

    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .password("12345QWEQNiosjfof")
                .name(name)
                .phoneNumber("010-1234-5678")
                .role(Role.CUSTOMER)
                .build();
    }

}