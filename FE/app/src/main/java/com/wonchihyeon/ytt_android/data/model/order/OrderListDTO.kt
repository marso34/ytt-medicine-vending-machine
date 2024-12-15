package com.wonchihyeon.ytt_android.data.model.order

data class OrderListDTO(
    val id: String,
    val machineId: Int,
    val machineName: String,
    val orderState: String,
    val orderAt: String,
    val totalPrice: Int,
)