package com.wonchihyeon.ytt_android.data.model.order

data class OrderStateDTO (
    val id: String,
    val machineId: Int,
    val machineName: String,
    val orderState: OrderState,
    val orderAt: String,
    val totalPrice: Int
)