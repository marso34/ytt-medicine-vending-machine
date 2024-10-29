package com.wonchihyeon.ytt_android.data.model

data class medicine(
    val medicine_id: Int,
    val name: Char,
    val product_code: Char,
    val manufacturer: Char,
    val efficacy: String,
    val usages: String,
    val precautions: Int,
    val validity_period: Char,
    val image_url: Char,
    val price: Int,
)
