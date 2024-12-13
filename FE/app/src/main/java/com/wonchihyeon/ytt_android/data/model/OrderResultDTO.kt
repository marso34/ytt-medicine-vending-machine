package com.wonchihyeon.ytt_android.data.model

data class OrderResultDTO(
    val id: String,
    val result: Boolean,
    val orderState: OrderState,
    val orderItem: List<OrderDetailDTO>
)