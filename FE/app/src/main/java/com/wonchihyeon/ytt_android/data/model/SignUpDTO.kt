package com.wonchihyeon.ytt_android.data.model

data class SignUpDTO(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val role: Role,
    val accessToken: String? = null,   // 서버 응답에서 포함되는 Access Token
    val refreshToken: String? = null   // 서버 응답에서 포함되는 Refresh Token
)
