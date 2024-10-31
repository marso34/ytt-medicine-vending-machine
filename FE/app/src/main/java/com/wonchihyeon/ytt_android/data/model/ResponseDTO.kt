package com.wonchihyeon.ytt_android.data.model

data class ResponseDTO<T> (
    val code: String,
    val message: String,
    val body: T?
)
