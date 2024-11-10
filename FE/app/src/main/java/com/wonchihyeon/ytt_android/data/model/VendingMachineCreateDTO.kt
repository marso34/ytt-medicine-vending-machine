package com.wonchihyeon.ytt_android.data.model

data class VendingMachineCreateDTO (
    val name: String,
    val location: String,
    val latitude: Double,
    val longtitude: Double,
    val quantity: Int,
    )