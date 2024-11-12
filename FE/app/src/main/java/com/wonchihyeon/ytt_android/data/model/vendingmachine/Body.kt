package com.wonchihyeon.ytt_android.data.model.vendingmachine


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("address")
    val address: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: Int,
    @SerializedName("longitude")
    val longitude: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: String
)