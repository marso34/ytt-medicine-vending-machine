package com.wonchihyeon.ytt_android.data.model

data class VendingMachineDetailDTO(
    val id: Int,
    val name: String,
    val state: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val isFavorite: Boolean,
    val medicines: List<Medicine>,
)
