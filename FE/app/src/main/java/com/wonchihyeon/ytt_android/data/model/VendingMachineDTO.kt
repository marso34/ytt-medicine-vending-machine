package com.wonchihyeon.ytt_android.data.model

data class VendingMachineDTO(
    val id: Int,
    val name: String,
    val state: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
