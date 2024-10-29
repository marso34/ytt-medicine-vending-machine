package com.wonchihyeon.ytt_android.data.model

data class ingredient(
    val ingredient_id: Int,
    val name: Char,
    val efficacy: String,
    val pharmacopeia: Pharmacopeia,
)

enum class Pharmacopeia() {
    KP,
    KPC,
    KHP,
    USP,
    JP,
    EP,
    BP,
    DAB,
    PF,
}