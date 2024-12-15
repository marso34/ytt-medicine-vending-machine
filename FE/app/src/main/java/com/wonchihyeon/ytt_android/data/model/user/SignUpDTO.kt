package com.wonchihyeon.ytt_android.data.model.user

data class SignUpDTO(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val role: String,
)
