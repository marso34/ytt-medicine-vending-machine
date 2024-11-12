package com.example.ytt.domain.order.controller;


import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.OrderReqDto;
import com.example.ytt.domain.order.service.OrderService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문 기본", description = "주문 생성, API")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    @SwaggerApi(summary = "주문 생성", description = "주문 생성 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDto>> createOrder(@RequestBody OrderReqDto reqDto) {
        return ResponseUtil.success(orderService.createOrder(reqDto));
    }

    // 결제여부, 자판기 재고 여부, 자판기 보관함 저장여부 따라서 주문 성공 반환하도록

}
