package com.wonchihyeon.ytt_android.data.model

data class UserDTO (
    val userid: Int,
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val role: String,
)