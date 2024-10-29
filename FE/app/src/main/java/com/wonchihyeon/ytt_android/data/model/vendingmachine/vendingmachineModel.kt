package com.wonchihyeon.ytt_android.data.model.vendingmachine


import com.google.gson.annotations.SerializedName

data class vendingmachineModel(
    @SerializedName("body")
    val body: List<Body>,
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)