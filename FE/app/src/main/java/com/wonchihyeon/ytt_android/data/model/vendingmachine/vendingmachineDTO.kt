package com.wonchihyeon.ytt_android.data.model.vendingmachine

import android.graphics.Point
import java.sql.Timestamp

data class vendingmachineDTO(
    val id : Int,
    val name: String,
    val address: String,
    val location: Point,
    val capacity: Int,
    val created_at: Timestamp,
    val modified_at: Timestamp,
    val state: status,
)

enum class status() {
    OPERATING,
    OUT_OF_STOCK,
    ERROR,
    WARNING,
    MAINTENANCE,
    UNKNOWN,
}