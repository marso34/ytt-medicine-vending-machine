package com.example.ytt.domain.order.controller;

import com.example.ytt.domain.order.dto.machine.OrderResultDto;
import com.example.ytt.domain.order.service.OrderManageService;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문 처리 결과", description = "주문 처리 결과 API (자판기)")
public class OrderMachineController {

    private final OrderManageService orderManageService;

    /* 주문 처리 결과 : 자판기 */

    @PostMapping("/store/{orderId}")
    @SwaggerApi(summary = "자판기에서 약 처리 결과", description = "자판기에서 주문한 약에 대한 처리 결과(보관 완료, 보관 실패) API")
    public ResponseEntity<ResponseDto<OrderResultDto>> storeOrder(@PathVariable(value = "orderId") String orderId, @RequestBody OrderResultDto storeResult) {
        return ResponseUtil.success(orderManageService.processStoreResult(orderId, storeResult));
    }

    @PostMapping("/complete/{orderId}")
    @SwaggerApi(summary = "자판기에서 약 수령 완료", description = "사용자가 약 수령 후 주문 완료 처리 API")
    public ResponseEntity<ResponseDto<OrderResultDto>> completeOrder(@PathVariable(value = "orderId") String orderId) {
        return ResponseUtil.success(orderManageService.completeOrder(orderId));
    }

}
