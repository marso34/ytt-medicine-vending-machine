package com.wonchihyeon.ytt_android.data.model.user

import java.math.BigInteger
import java.security.Timestamp

data class User(
    val id: BigInteger,
    val name: Char,
    val email: Char,
    val password: Char,
    val phone_number: Char,
    val created: Timestamp,
    val modified: Timestamp,
    val role: Role,
)
enum class Role() {
    ADMIN,
    CUSTOMER,
    MANAGER,
}
