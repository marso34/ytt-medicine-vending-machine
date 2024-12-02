package com.example.ytt.domain.order.service;

import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.example.ytt.domain.order.domain.OrderItem;
import com.example.ytt.domain.order.dto.request.OrderItemReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderDetailService {

    // TODO: 같은 상품, 같은 개수 엔터티에 이미 존재하면 매번 새로 만들지말고 가져다 쓰도록 추가

    private final MedicineRepository medicineRepository;

    public OrderItem createOrderDetail(OrderItemReqDto orderDetailReqDto) {
        Medicine medicine = medicineRepository.findByProductCode(orderDetailReqDto.productCode())
                .orElseThrow(() -> new IllegalArgumentException("약품을 찾을 수 없습니다: " + orderDetailReqDto.productCode()));

        return OrderItem.builder()
                .medicine(medicine)
                .quantity(orderDetailReqDto.quantity())
                .build();
    }
}
