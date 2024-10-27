package com.wonchihyeon.ytt_android.data.model

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val role: Role,
)

enum class Role {
    CUSTOMER,
    MANAGER,
    ADMIN
}


