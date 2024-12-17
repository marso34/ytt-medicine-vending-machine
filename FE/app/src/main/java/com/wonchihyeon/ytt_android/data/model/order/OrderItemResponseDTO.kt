package com.wonchihyeon.ytt_android.data.model.order

data class OrderItemResponseDTO (
    val id: String,
    val machineId: Int,
    val machineName: String,
    val productCode: String,
    val imageURL: String,
    val quantity: Int,
    val totalPrice: Int,
)
