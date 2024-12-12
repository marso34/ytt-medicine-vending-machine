package com.wonchihyeon.ytt_android.data.model

data class OrderDTO (
    val userId: Int,
    val vendingMachineId: Int,
    val orderItems: List<OrderDetailDTO>
)

