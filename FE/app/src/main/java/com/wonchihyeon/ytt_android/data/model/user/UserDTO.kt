package com.wonchihyeon.ytt_android.data.model.user

data class UserDTO (
    val userid: Int,
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val role: String,
)