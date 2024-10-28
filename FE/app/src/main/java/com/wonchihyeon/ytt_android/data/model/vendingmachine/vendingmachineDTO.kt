package com.wonchihyeon.ytt_android.data.model.vendingmachine

data class vendingmachineDTO(
    val id : Int,
    val name: String,
    val state: status,
    val address: String,
    val latitutde: Number,
    val longtitude: Number,
)

enum class status() {
    OPERATING,
    OUT_OF_STOCK,
    ERROR,
    WARNING,
    MAINTENANCE,
    UNKNOWN,
}