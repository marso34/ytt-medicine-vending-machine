package com.example.ytt.domain.order.controller;

import com.example.ytt.domain.order.dto.OrderDetailDto;
import com.example.ytt.domain.order.dto.OrderDto;
import com.example.ytt.domain.order.dto.request.OrderReqDto;
import com.example.ytt.domain.order.service.OrderFindService;
import com.example.ytt.domain.order.service.OrderManageService;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문 기본", description = "주문 생성, 조회 API")
public class OrderController {

    private final OrderManageService orderManageService;
    private final OrderFindService orderFindService;

    /* 주문 조회 */

    @GetMapping
    @SwaggerApi(summary = "사용자의 주문 목록 조회", description = "로그인된 사용자의 모든 주문 목록을 조회합니다. (state: 주문 상태에 따라 조회, null: 전체 주문 조회)")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getOrdersForUser(
            @Parameter(description = "주문 상태", example = "OPERATING")
            @RequestParam(value = "state", required = false) String state,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseUtil.success(orderFindService.getOrdersByUser(user.getId(), state));
    }

    @GetMapping("/all")
    @SwaggerApi(summary = "모든 주문 목록 조회", description = "모든 주문 목록을 조회합니다. (state: 주문 상태에 따라 조회, null: 전체 주문 조회)")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getAllOrders(
            @Parameter(description = "주문 상태", example = "OPERATING")
            @RequestParam(value = "state", required = false) String state,
            @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER,"주문 조회 권한이 없습니다.", null);
        }

        return ResponseUtil.success(orderFindService.getOrdersByUser(null, state));
    }

    @GetMapping("/machine")
    @SwaggerApi(summary = "특정 자판기의 모든 주문 목록 조회", description = "모든 주문 목록을 조회합니다. (state: 주문 상태에 따라 조회, null: 전체 주문 조회)")
    public ResponseEntity<ResponseDto<List<OrderDto>>> getOrdersByMachine(
            @Parameter(description = "주문 상태", example = "OPERATING")
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "id") long machineId, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER,"주문 조회 권한이 없습니다.", null);
        }

        return ResponseUtil.success(orderFindService.getOrdersByVendingMachine(machineId, state));
    }

    @GetMapping("/{orderId}")
    @SwaggerApi(summary = "주문 상세 목록 조회", description = "로그인된 사용자의 모든 주문 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<OrderDetailDto>> getOrderDetailForUser(@PathVariable(value = "orderId") String orderId, @AuthenticationPrincipal CustomUserDetails user) {
        if (!orderFindService.isOrderOwner(orderId, user.getId()) && user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER,"주문 조회 권한이 없습니다.", null);
        }

        return ResponseUtil.success(orderFindService.getOrderDetail(orderId));
    }

    /* 주문 생성, 취소 : 유저 */

    @PostMapping("/create")
    @SwaggerApi(summary = "주문 생성", description = "주문 생성 API")
    public ResponseEntity<ResponseDto<OrderDetailDto>> createOrder(@RequestBody OrderReqDto reqDto) {
        return ResponseUtil.success(orderManageService.createOrder(reqDto));
    }

    @PostMapping("/cancel/{orderId}")
    @SwaggerApi(summary = "주문 취소", description = "주문 취소 처리 API", implementation = ResponseDto.class)
    public ResponseEntity<ResponseDto<OrderDetailDto>> cancelOrder(@PathVariable(value = "orderId") String orderId) {
        return ResponseUtil.success(orderManageService.cancelOrder(orderId));
    }

}
