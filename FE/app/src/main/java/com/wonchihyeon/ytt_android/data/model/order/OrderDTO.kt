package com.wonchihyeon.ytt_android.data.model.order

data class OrderDTO (
    val userId: Int,
    val vendingMachineId: Int,
    val orderItems: List<OrderDetailDTO>
)

