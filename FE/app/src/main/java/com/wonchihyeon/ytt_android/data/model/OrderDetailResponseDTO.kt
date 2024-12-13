package com.wonchihyeon.ytt_android.data.model

data class OrderDetailResponseDTO (
    val id: String,
    val userId: Int,
    val machineId: Int,
    val machineName: String,
    val orderState: OrderState,
    val orderAt: String,
    val completedAt: String,
    val totalPrice: Int,
    val orderItems: List<OrderItemResponseDTO>
)