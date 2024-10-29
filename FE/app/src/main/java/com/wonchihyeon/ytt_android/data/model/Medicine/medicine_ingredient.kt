package com.wonchihyeon.ytt_android.data.model

data class medicine_ingredient(
    val medicine_ingredient_id: Int,
    val quantity: Double,
    val unit: Unit,
    val ingredient_id: Int,
    val medicine_id: Int,
)

enum class Unit() {
    MG,
    G,
    ML,
    L,
    EA,
}
