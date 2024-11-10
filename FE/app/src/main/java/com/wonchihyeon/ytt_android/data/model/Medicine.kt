package com.wonchihyeon.ytt_android.data.model

data class Medicine (
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val stock: Int,
    val imageURL: String
)