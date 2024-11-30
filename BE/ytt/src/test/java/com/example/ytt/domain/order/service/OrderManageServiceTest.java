package com.example.ytt.domain.order.service;


import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.model.Address;
import com.example.ytt.domain.order.domain.Order;
import com.example.ytt.domain.order.domain.OrderItem;
import com.example.ytt.domain.order.dto.request.OrderItemReqDto;
import com.example.ytt.domain.order.repository.OrderRepository;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.domain.vendingmachine.domain.MachineState;
import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import com.example.ytt.domain.vendingmachine.repository.VendingMachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@SpringBootTest
@Transactional
class OrderManageServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private VendingMachineRepository vendingMachineRepository;
    @Autowired
    OrderManageService orderManageService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailService orderDetailService;


    private User user;
    private Medicine medicine1;
    private Medicine medicine2;
    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        user = createUser("example@naver.com", "홍길동");
        medicine1 = createMedicine("판피린", "productCode1",2000);
        medicine2 = createMedicine("타이레놀", "productCode2",1000);
        vendingMachine = createVendingMachine("강릉원주대 자판기1", Address.of("address1", 37.305121, 127.922653));

        user = userRepository.save(user);
        medicine1 = medicineRepository.save(medicine1);
        medicine2 = medicineRepository.save(medicine2);
        vendingMachine = vendingMachineRepository.save(vendingMachine);
    }

    @Test
    @DisplayName("상품_정상_주문_테스트")
    public void 상품_정상_주문_테스트() throws Exception {
        OrderItemReqDto orderDetailReqDto1 = new OrderItemReqDto(medicine1.getProductCode(), 2); // 2개 주문
        OrderItemReqDto orderDetailReqDto2 = new OrderItemReqDto(medicine2.getProductCode(), 1); // 1개 주문

        List<OrderItemReqDto> orderItems = List.of(orderDetailReqDto1, orderDetailReqDto2);

        Order order = createOrder(user, vendingMachine, orderItems);

        System.out.println("Order ID: " + order.getId());
        System.out.println("Total Order Price: " + order.getTotalPrice());

    }

    // Medicine 생성 메서드
    private Medicine createMedicine(String name, String productCode, int price) {
        return  Medicine.builder()
                .name(name)
                .productCode(productCode)
                .manufacturer("manufacturer")
                .efficacy("efficacy")
                .usages("usage")
                .precautions("precautions")
                .validityPeriod("validityPeriod")
                .price(price)
                .build();
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

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .password("12345QWEQNiosjfof")
                .name(name)
                .phoneNumber("010-1234-5678")
                .role(Role.CUSTOMER)
                .build();
    }

    public Order createOrder(User user, VendingMachine vendingMachine, List<OrderItemReqDto> orderItemDtos) {
        List<OrderItem> orderItems = orderItemDtos.stream()
                .map(orderDetailService::createOrderDetail)
                .toList();

        Order order = Order.builder()
                .user(user)
                .vendingMachine(vendingMachine)
                .orderItems(orderItems)
                .build();

        return orderRepository.save(order);
    }

}